package algorithm.rule;

import utils.concept.Concept;
import utils.concept.Concept_SE_ISI;
import utils.item.ObjectIdMapper;
import utils.rule.ThreeWayRuleCandidate;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

/** Combines condition and decision concepts using X subseteq Y. */
public final class ThreeWayRuleCandidateGenerator {
    private ThreeWayRuleCandidateGenerator() {
    }

    public static List<ThreeWayRuleCandidate> generate(
            Collection<Concept_SE_ISI> conditionConcepts,
            Collection<Concept> decisionConcepts,
            ObjectIdMapper objectIdMapper,
            int objectCount
    ) {
        BitSet universe = new BitSet();
        universe.set(1, objectCount + 1);

        List<ThreeWayRuleCandidate> rules = new ArrayList<>();
        for (Concept decisionConcept : decisionConcepts) {
            BitSet decisionExtent = decisionConcept.getExtent();

            // Definition 4 requires the decision extent to be different from U.
            if (decisionExtent.equals(universe)) {
                continue;
            }

            BitSet decisionOriginalExtent = objectIdMapper.toOriginalSet(decisionExtent);
            for (Concept_SE_ISI conditionConcept : conditionConcepts) {
                // A rule must be supported by at least one condition-side object.
                if (conditionConcept.getExtent().isEmpty()) {
                    continue;
                }

                if (!isSubsetOf(conditionConcept.getIntent_min(), conditionConcept.getIntent_max())) {
                    throw new IllegalStateException(
                            "Invalid condition concept: B_low is not a subset of B_high"
                    );
                }

                BitSet conditionOriginalExtent =
                        objectIdMapper.toOriginalSet(conditionConcept.getExtent());

                if (!isSubsetOf(conditionOriginalExtent, decisionOriginalExtent)) {
                    continue;
                }

                rules.add(new ThreeWayRuleCandidate(
                        conditionConcept.getExtent(),
                        conditionOriginalExtent,
                        conditionConcept.getIntent_min(),
                        conditionConcept.getIntent_max(),
                        decisionExtent,
                        decisionOriginalExtent,
                        decisionConcept.getIntent()
                ));
            }
        }
        return rules;
    }

    public static boolean isSubsetOf(BitSet subset, BitSet superset) {
        BitSet remainder = (BitSet) subset.clone();
        remainder.andNot(superset);
        return remainder.isEmpty();
    }
}
