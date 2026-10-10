from dataclasses import dataclass
from itertools import combinations
from typing import Set, List, Tuple, Dict


@dataclass
class Concept:
    name: str
    extent: Set[int]
    intent_min: Set[str]
    intent_max: Set[str]


@dataclass
class ThreeWayRule:
    name: str
    antecedent_concept: str
    intent_min: Set[str]
    intent_max: Set[str]
    decision: Set[str]


def A(nums):
    """把 {2,3,9} 转成 {'a2','a3','a9'}"""
    return {f"a{i}" for i in nums}


def format_set(s: Set[str]) -> str:
    if not s:
        return "∅"
    return "{" + ",".join(sorted(s, key=lambda x: int(x[1:]))) + "}"


def format_interval(intent_min: Set[str], intent_max: Set[str]) -> str:
    return f"[{format_set(intent_min)}, {format_set(intent_max)}]"


def discernibility(child: Concept, parent: Concept) -> Set[str]:
    return (child.intent_min - parent.intent_min) | (child.intent_max - parent.intent_max)


def print_discernibility_pairs(pairs: List[Tuple[Concept, Concept]]) -> List[Set[str]]:
    dis_sets = []

    print("\n辨识矩阵非空项 Λ:")
    print(f"{'child':<8} {'parent':<10} {'DIS(child,parent)':<20}")

    for child, parent in pairs:
        dis = discernibility(child, parent)
        dis_sets.append(dis)
        print(f"{child.name:<8} {parent.name:<10} {format_set(dis):<20}")

    return dis_sets


def build_discernibility_matrix(
    row_concepts: List[Concept],
    col_concepts: List[Concept],
    direct_parent_pairs: List[Tuple[Concept, Concept]]
) -> Dict[Tuple[str, str], Set[str]]:
    pair_names = {
        (child.name, parent.name)
        for child, parent in direct_parent_pairs
    }

    matrix = {}

    for row in row_concepts:
        for col in col_concepts:
            if (row.name, col.name) in pair_names:
                matrix[(row.name, col.name)] = discernibility(row, col)
            else:
                matrix[(row.name, col.name)] = set()

    return matrix


def print_discernibility_matrix(
    row_concepts: List[Concept],
    col_concepts: List[Concept],
    matrix: Dict[Tuple[str, str], Set[str]]
) -> List[Set[str]]:
    dis_sets = []

    print("\n辨识矩阵 Λ:")
    print(" " * 10 + "  ".join(f"{concept.name:>10}" for concept in col_concepts))

    for row in row_concepts:
        values = [f"{row.name:>10}"]

        for col in col_concepts:
            dis = matrix[(row.name, col.name)]
            values.append(f"{format_set(dis):>10}")

            if dis:
                dis_sets.append(dis)

        print("  ".join(values))

    return dis_sets


def print_discernibility_function(dis_sets: List[Set[str]]) -> None:
    clauses = []
    for s in dis_sets:
        if len(s) == 1:
            clauses.append(next(iter(s)))
        else:
            clauses.append("(" + " ∨ ".join(sorted(s, key=lambda x: int(x[1:]))) + ")")

    print("\n辨识函数 f(Λ):")
    print("f(Λ) = " + " ∧ ".join(clauses))


def compute_reducts(dis_sets: List[Set[str]]) -> List[Set[str]]:
    dis_sets = [set(s) for s in dis_sets if s]

    if not dis_sets:
        return [set()]

    all_attrs = sorted(set().union(*dis_sets), key=lambda x: int(x[1:]))

    reducts = []

    for r in range(1, len(all_attrs) + 1):
        for comb in combinations(all_attrs, r):
            candidate = set(comb)

            if not all(candidate & dis for dis in dis_sets):
                continue

            if any(red <= candidate for red in reducts):
                continue

            reducts = [red for red in reducts if not candidate < red]
            reducts.append(candidate)

    return reducts


def classify_attributes(reducts: List[Set[str]], all_attrs: Set[str]) -> None:
    union_red = set().union(*reducts)
    inter_red = set(reducts[0])

    for red in reducts[1:]:
        inter_red &= red

    core = inter_red
    relative = union_red - inter_red
    unnecessary = all_attrs - union_red

    print("\n三类属性:")
    print("核心属性 CO(IK):", format_set(core))
    print("相对必要属性 RN(IK):", format_set(relative))
    print("绝对不必要属性 AU(IK):", format_set(unnecessary))


def generate_rules(children: List[Concept], decision: Set[str]) -> List[ThreeWayRule]:
    rules = []

    for child in children:
        rules.append(
            ThreeWayRule(
                name=f"r_{child.name}",
                antecedent_concept=child.name,
                intent_min=set(child.intent_min),
                intent_max=set(child.intent_max),
                decision=set(decision)
            )
        )

    return rules


def generate_rules_by_decision(
    children: List[Concept],
    decision_by_concept: Dict[str, Set[str]]
) -> List[ThreeWayRule]:
    rules = []

    for child in children:
        rules.append(
            ThreeWayRule(
                name=f"r_{child.name}",
                antecedent_concept=child.name,
                intent_min=set(child.intent_min),
                intent_max=set(child.intent_max),
                decision=set(decision_by_concept[child.name])
            )
        )

    return rules


def unique_sets(sets: List[Set[str]]) -> List[Set[str]]:
    unique = []
    seen = set()

    for item in sets:
        key = tuple(sorted(item, key=lambda x: int(x[1:])))
        if key in seen:
            continue

        seen.add(key)
        unique.append(item)

    return unique


def unique_parents_from_pairs(pairs: List[Tuple[Concept, Concept]]) -> List[Concept]:
    parents = []
    seen = set()

    for _, parent in pairs:
        if parent.name in seen:
            continue

        seen.add(parent.name)
        parents.append(parent)

    return parents


def project_rule_by_reduct(rule: ThreeWayRule, reduct: Set[str]) -> ThreeWayRule:
    return ThreeWayRule(
        name=rule.name,
        antecedent_concept=rule.antecedent_concept,
        intent_min=rule.intent_min & reduct,
        intent_max=rule.intent_max & reduct,
        decision=set(rule.decision)
    )


def print_rules(rules: List[ThreeWayRule], title: str) -> None:
    print(f"\n{title}:")
    for r in rules:
        print(f"{r.name}: {format_interval(r.intent_min, r.intent_max)} -> {format_set(r.decision)}")


# ============================================================
# Corrected 8 non-redundant rules and their direct parents
# ============================================================


# ------------------------------------------------------------
# NR 1
# [{a3,a4,a5}, {a1,a2,a3,a4,a5,a8}] -> {d3}
# X = {3,7}
# ------------------------------------------------------------
s1 = Concept(
    name="s1",
    extent={3, 7},
    intent_min=A({3, 4, 5}),
    intent_max=A({1, 2, 3, 4, 5, 8})
)

p11 = Concept(
    name="p11",
    extent={3, 4, 7, 8},
    intent_min=A({3, 4, 5}),
    intent_max=A({1, 2, 3, 4, 5})
)

p12 = Concept(
    name="p12",
    extent={3, 7, 17},
    intent_min=A({3, 4}),
    intent_max=A({3, 4, 8})
)

p13 = Concept(
    name="p13",
    extent={3, 7, 18},
    intent_min=A({4, 5}),
    intent_max=A({1, 2, 4, 5, 8})
)


# ------------------------------------------------------------
# NR 2
# [{a5,a6}, {a5,a6,a8}] -> {d3}
# X = {3,11}
# ------------------------------------------------------------
s2 = Concept(
    name="s2",
    extent={3, 11},
    intent_min=A({5, 6}),
    intent_max=A({5, 6, 8})
)

p21 = Concept(
    name="p21",
    extent={3, 4, 5, 8, 10, 11, 12, 20},
    intent_min=A({5, 6}),
    intent_max=A({5, 6})
)

p22 = Concept(
    name="p22",
    extent={3, 7, 11, 18},
    intent_min=A({5}),
    intent_max=A({5, 8})
)


# ------------------------------------------------------------
# NR 3
# [{a3,a5}, {a3,a5,a9}] -> {d1}
# X = {4,5,7,9,20}
# ------------------------------------------------------------
s3 = Concept(
    name="s3",
    extent={4, 5, 7, 9, 20},
    intent_min=A({3, 5}),
    intent_max=A({3, 5, 9})
)

p31 = Concept(
    name="p31",
    extent={3, 4, 5, 7, 8, 9, 20},
    intent_min=A({3, 5}),
    intent_max=A({3, 5})
)

p32 = Concept(
    name="p32",
    extent={4, 5, 7, 9, 10, 11, 12, 20},
    intent_min=A({5}),
    intent_max=A({5, 9})
)


# ------------------------------------------------------------
# NR 4
# [{a2,a3,a4,a5}, {a1,a2,a3,a4,a5}] -> {d1}
# X = {4,7,8}
# ------------------------------------------------------------
s4 = Concept(
    name="s4",
    extent={4, 7, 8},
    intent_min=A({2, 3, 4, 5}),
    intent_max=A({1, 2, 3, 4, 5})
)

p41 = Concept(
    name="p41",
    extent={3, 4, 7, 8},
    intent_min=A({3, 4, 5}),
    intent_max=A({1, 2, 3, 4, 5})
)

p42 = Concept(
    name="p42",
    extent={4, 7, 8, 10},
    intent_min=A({2, 4, 5}),
    intent_max=A({1, 2, 4, 5})
)


# ------------------------------------------------------------
# NR 5
# [{a8}, {a8}] -> {d1}
# X = {7,11,17}
# ------------------------------------------------------------
s5 = Concept(
    name="s5",
    extent={7, 11, 17},
    intent_min=A({8}),
    intent_max=A({8})
)

p51 = Concept(
    name="p51",
    extent={3, 7, 11, 17, 18},
    intent_min=A({}),
    intent_max=A({8})
)


# ------------------------------------------------------------
# NR 6
# [∅, {a3,a4,a6,a7}] -> {d1}
# X = {8,15,16}
# ------------------------------------------------------------
s6 = Concept(
    name="s6",
    extent={8, 15, 16},
    intent_min=A({}),
    intent_max=A({3, 4, 6, 7})
)

p61 = Concept(
    name="p61",
    extent={3, 4, 8, 9, 14, 15, 16, 20},
    intent_min=A({}),
    intent_max=A({3, 4, 6})
)


# ------------------------------------------------------------
# NR 7
# [{a5,a8,a9}, {a5,a8,a9}] -> {d1,d3}
# X = {7,11}
# ------------------------------------------------------------
s7 = Concept(
    name="s7",
    extent={7, 11},
    intent_min=A({5, 8, 9}),
    intent_max=A({5, 8, 9})
)

p71 = Concept(
    name="p71",
    extent={3, 7, 11, 18},
    intent_min=A({5}),
    intent_max=A({5, 8})
)

p72 = Concept(
    name="p72",
    extent={4, 7, 9, 10, 11, 12, 20},
    intent_min=A({5, 9}),
    intent_max=A({5, 9})
)

p73 = Concept(
    name="p73",
    extent={7, 11, 17},
    intent_min=A({8}),
    intent_max=A({8})
)


# ------------------------------------------------------------
# NR 8
# [{a5,a6,a8,a9}, {a5,a6,a8,a9}] -> {d1,d2,d3}
# X = {11}
# ------------------------------------------------------------
s8 = Concept(
    name="s8",
    extent={11},
    intent_min=A({5, 6, 8, 9}),
    intent_max=A({5, 6, 8, 9})
)

p81 = Concept(
    name="p81",
    extent={3, 11},
    intent_min=A({5, 6}),
    intent_max=A({5, 6, 8})
)

p82 = Concept(
    name="p82",
    extent={4, 10, 11, 12, 20},
    intent_min=A({5, 6, 9}),
    intent_max=A({5, 6, 9})
)

p83 = Concept(
    name="p83",
    extent={7, 11},
    intent_min=A({5, 8, 9}),
    intent_max=A({5, 8, 9})
)


# ============================================================
# All active NR antecedent concepts
# ============================================================

children = [
    s1,
    s2,
    s3,
    s4,
    s5,
    s6,
    s7,
    s8
]


# ============================================================
# All direct parent concepts
# ============================================================

parents = [
    p11, p12, p13,
    p21, p22,
    p31, p32,
    p41, p42,
    p51,
    p61,
    p71, p72, p73,
    p81, p82, p83
]


# ============================================================
# Direct child-parent relations
# ============================================================

parent_pairs = [
    # NR 1
    (s1, p11),
    (s1, p12),
    (s1, p13),

    # NR 2
    (s2, p21),
    (s2, p22),

    # NR 3
    (s3, p31),
    (s3, p32),

    # NR 4
    (s4, p41),
    (s4, p42),

    # NR 5
    (s5, p51),

    # NR 6
    (s6, p61),

    # NR 7
    (s7, p71),
    (s7, p72),
    (s7, p73),

    # NR 8
    (s8, p81),
    (s8, p82),
    (s8, p83)
]


# ============================================================
# Decision labels of the corrected 8 NR rules
# ============================================================

decision_by_concept = {
    s1.name: {"d3"},
    s2.name: {"d3"},

    s3.name: {"d1"},
    s4.name: {"d1"},
    s5.name: {"d1"},
    s6.name: {"d1"},

    s7.name: {"d1", "d3"},

    s8.name: {"d1", "d2", "d3"}
}



# # Original incomplete formal decision context NR
# # ------------------------------------------------------------
# # NR 1
# # [{a3,a5}, {a3,a5,a9}] -> {d1}
# # X = {1,4,5,6,7,9,19,20}
# # ------------------------------------------------------------
# s1 = Concept(
#     name="s1",
#     extent={1, 4, 5, 6, 7, 9, 19, 20},
#     intent_min=A({3, 5}),
#     intent_max=A({3, 5, 9})
# )
#
# p11 = Concept(
#     name="p11",
#     extent={1, 3, 4, 5, 6, 7, 8, 9, 19, 20},
#     intent_min=A({3, 5}),
#     intent_max=A({3, 5})
# )
#
# p12 = Concept(
#     name="p12",
#     extent={1, 4, 5, 6, 7, 9, 10, 11, 12, 19, 20},
#     intent_min=A({5}),
#     intent_max=A({5, 9})
# )
#
#
# # ------------------------------------------------------------
# # NR 2
# # [{a2,a3,a4,a5}, {a1,a2,a3,a4,a5}] -> {d1}
# # X = {1,4,6,7,8,19}
# # ------------------------------------------------------------
# s2 = Concept(
#     name="s2",
#     extent={1, 4, 6, 7, 8, 19},
#     intent_min=A({2, 3, 4, 5}),
#     intent_max=A({1, 2, 3, 4, 5})
# )
#
# p21 = Concept(
#     name="p21",
#     extent={1, 3, 4, 6, 7, 8, 19},
#     intent_min=A({3, 4, 5}),
#     intent_max=A({1, 2, 3, 4, 5})
# )
#
# p22 = Concept(
#     name="p22",
#     extent={1, 4, 6, 7, 8, 10, 19},
#     intent_min=A({2, 4, 5}),
#     intent_max=A({1, 2, 4, 5})
# )
#
#
# # ------------------------------------------------------------
# # NR 3
# # [{a8}, {a8}] -> {d1}
# # X = {1,6,7,11,17,19}
# # ------------------------------------------------------------
# s3 = Concept(
#     name="s3",
#     extent={1, 6, 7, 11, 17, 19},
#     intent_min=A({8}),
#     intent_max=A({8})
# )
#
# p31 = Concept(
#     name="p31",
#     extent={1, 3, 6, 7, 11, 17, 18, 19},
#     intent_min=A({}),
#     intent_max=A({8})
# )
#
#
# # ------------------------------------------------------------
# # NR 4
# # [∅, {a3,a4,a6,a7}] -> {d1}
# # X = {1,6,8,15,16,19}
# # ------------------------------------------------------------
# s4 = Concept(
#     name="s4",
#     extent={1, 6, 8, 15, 16, 19},
#     intent_min=A({}),
#     intent_max=A({3, 4, 6, 7})
# )
#
# p41 = Concept(
#     name="p41",
#     extent={1, 3, 4, 6, 8, 9, 14, 15, 16, 19, 20},
#     intent_min=A({}),
#     intent_max=A({3, 4, 6})
# )
#
#
# # ------------------------------------------------------------
# # NR 5
# # [{a5,a6,a8,a9}, {a5,a6,a8,a9}] -> {d1,d2}
# # X = {1,6,11,19}
# # ------------------------------------------------------------
# s5 = Concept(
#     name="s5",
#     extent={1, 6, 11, 19},
#     intent_min=A({5, 6, 8, 9}),
#     intent_max=A({5, 6, 8, 9})
# )
#
# p51 = Concept(
#     name="p51",
#     extent={1, 3, 6, 11, 19},
#     intent_min=A({5, 6}),
#     intent_max=A({5, 6, 8})
# )
#
# p52 = Concept(
#     name="p52",
#     extent={1, 4, 6, 10, 11, 12, 19, 20},
#     intent_min=A({5, 6, 9}),
#     intent_max=A({5, 6, 9})
# )
#
# p53 = Concept(
#     name="p53",
#     extent={1, 6, 7, 11, 19},
#     intent_min=A({5, 8, 9}),
#     intent_max=A({5, 8, 9})
# )
#
#
# # ============================================================
# # All active NR antecedent concepts
# # ============================================================
#
# children = [
#     s1,
#     s2,
#     s3,
#     s4,
#     s5
# ]
#
#
# # ============================================================
# # All direct parent concepts
# # ============================================================
#
# parents = [
#     p11, p12,
#     p21, p22,
#     p31,
#     p41,
#     p51, p52, p53
# ]
#
#
# # ============================================================
# # Direct child-parent relations
# # ============================================================
#
# parent_pairs = [
#     # NR 1
#     (s1, p11),
#     (s1, p12),
#
#     # NR 2
#     (s2, p21),
#     (s2, p22),
#
#     # NR 3
#     (s3, p31),
#
#     # NR 4
#     (s4, p41),
#
#     # NR 5
#     (s5, p51),
#     (s5, p52),
#     (s5, p53)
# ]
#
#
# # ============================================================
# # Decision labels of the 5 NR rules
# # ============================================================
#
# decision_by_concept = {
#     s1.name: {"d1"},
#     s2.name: {"d1"},
#     s3.name: {"d1"},
#     s4.name: {"d1"},
#     s5.name: {"d1", "d2"}
# }


all_attrs = A({1, 2, 3, 4, 5, 6, 7, 8, 9, 10})

rules = generate_rules_by_decision(children, decision_by_concept)
active_child_names = {rule.antecedent_concept for rule in rules}
active_children = [
    child
    for child in children
    if child.name in active_child_names
]
active_parent_pairs = [
    (child, parent)
    for child, parent in parent_pairs
    if child.name in active_child_names
]
active_parents = unique_parents_from_pairs(active_parent_pairs)

print_rules(rules, "全局非冗余三支规则 NR(IK)")

matrix = build_discernibility_matrix(active_children, active_parents, active_parent_pairs)
dis_sets = print_discernibility_matrix(active_children, active_parents, matrix)

reduction_dis_sets = unique_sets(dis_sets)

print("\n非空辨识属性集:")
for dis in reduction_dis_sets:
    print(format_set(dis))

print_discernibility_function(reduction_dis_sets)

reducts = compute_reducts(reduction_dis_sets)

print("\n全局条件约简 RED(IK):")
for i, red in enumerate(reducts, start=1):
    print(f"Reduct {i}: {format_set(red)}")

classify_attributes(reducts, all_attrs)

for i, red in enumerate(reducts, start=1):
    optimal_rules = [project_rule_by_reduct(rule, red) for rule in rules]
    print_rules(optimal_rules, f"基于 Reduct {i} 的全局最优三支规则 OR(IK)")
