package utils.rule;

import utils.concept.Concept_SE_ISI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleParentRelation {
    private final ThreeWayRuleCandidate rule;
    private final List<Concept_SE_ISI> directParents;

    public RuleParentRelation(
            ThreeWayRuleCandidate rule,
            List<Concept_SE_ISI> directParents
    ) {
        this.rule = rule;
        this.directParents = new ArrayList<>(directParents);
    }

    public ThreeWayRuleCandidate getRule() {
        return rule;
    }

    public List<Concept_SE_ISI> getDirectParents() {
        return Collections.unmodifiableList(directParents);
    }
}
