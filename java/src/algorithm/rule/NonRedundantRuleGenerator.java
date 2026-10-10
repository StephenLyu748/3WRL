package algorithm.rule;

import utils.rule.ThreeWayRuleCandidate;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import static algorithm.rule.ThreeWayRuleCandidateGenerator.isSubsetOf;

/** Extracts non-redundant rules according to Definitions 5 and 6. */
public final class NonRedundantRuleGenerator {
    private NonRedundantRuleGenerator() {
    }

    public static List<ThreeWayRuleCandidate> generate(
            Collection<ThreeWayRuleCandidate> candidateRules
    ) {
        List<ThreeWayRuleCandidate> uniqueRules = removeEquivalentRules(candidateRules);
        List<ThreeWayRuleCandidate> nonRedundantRules = new ArrayList<>();

        for (int targetIndex = 0; targetIndex < uniqueRules.size(); targetIndex++) {
            ThreeWayRuleCandidate target = uniqueRules.get(targetIndex);
            boolean redundant = false;

            for (int sourceIndex = 0; sourceIndex < uniqueRules.size(); sourceIndex++) {
                if (sourceIndex == targetIndex) {
                    continue;
                }

                ThreeWayRuleCandidate source = uniqueRules.get(sourceIndex);
                if (implies(source, target)) {
                    redundant = true;
                    break;
                }
            }

            if (!redundant) {
                nonRedundantRules.add(target);
            }
        }
        return nonRedundantRules;
    }

    /**
     * Definition 5: source implies target iff source's interval is contained
     * in target's interval and target.C is contained in source.C.
     */
    public static boolean implies(
            ThreeWayRuleCandidate source,
            ThreeWayRuleCandidate target
    ) {
        return isSubsetOf(source.getLowerIntent(), target.getLowerIntent())
                && isSubsetOf(source.getUpperIntent(), target.getUpperIntent())
                && isSubsetOf(target.getDecisionIntent(), source.getDecisionIntent());
    }

    private static List<ThreeWayRuleCandidate> removeEquivalentRules(
            Collection<ThreeWayRuleCandidate> candidateRules
    ) {
        List<ThreeWayRuleCandidate> uniqueRules = new ArrayList<>();
        for (ThreeWayRuleCandidate candidate : candidateRules) {
            boolean alreadyPresent = false;
            for (ThreeWayRuleCandidate existing : uniqueRules) {
                if (sameRule(candidate, existing)) {
                    alreadyPresent = true;
                    break;
                }
            }
            if (!alreadyPresent) {
                uniqueRules.add(candidate);
            }
        }
        return uniqueRules;
    }

    private static boolean sameRule(
            ThreeWayRuleCandidate left,
            ThreeWayRuleCandidate right
    ) {
        return equal(left.getLowerIntent(), right.getLowerIntent())
                && equal(left.getUpperIntent(), right.getUpperIntent())
                && equal(left.getDecisionIntent(), right.getDecisionIntent());
    }

    private static boolean equal(BitSet left, BitSet right) {
        return left.equals(right);
    }
}
