package algorithm.rule;

import utils.concept.Concept_SE_ISI;
import utils.rule.RuleParentRelation;
import utils.rule.ThreeWayRuleCandidate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static algorithm.concept.LabelConceptSelector.findDirectParentConcepts;

public final class NonRedundantRuleParentFinder {
    private NonRedundantRuleParentFinder() {
    }

    public static List<RuleParentRelation> find(
            Collection<ThreeWayRuleCandidate> nonRedundantRules,
            Collection<Concept_SE_ISI> allConditionConcepts
    ) {
        List<RuleParentRelation> relations = new ArrayList<>();
        for (ThreeWayRuleCandidate rule : nonRedundantRules) {
            List<Concept_SE_ISI> directParents = findDirectParentConcepts(
                    allConditionConcepts,
                    rule.getConditionExtent()
            );
            relations.add(new RuleParentRelation(rule, directParents));
        }
        return relations;
    }
}
