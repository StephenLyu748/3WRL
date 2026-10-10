package algorithm.concept;

import utils.Context;
import utils.concept.Concept_SE_ISI;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Queue;

import static utils.util.get_objs_max_shared;
import static utils.util.get_objs_min_shared;
import static utils.util.is_subset;
import static utils.util.makeSet;

public class ConceptRelation {
    public static Concept_SE_ISI getUniverseConcept(Context context) {
        BitSet universe = makeSet(context.getObjs_size());
        BitSet intentMin = get_objs_min_shared(context, universe);
        BitSet intentMax = get_objs_max_shared(context, universe);
        return new Concept_SE_ISI(universe, intentMin, intentMax);
    }

    public static List<Concept_SE_ISI> getDirectSubConceptsOfUniverse(
            Context context,
            Queue<Concept_SE_ISI> concepts
    ) {
        BitSet universe = makeSet(context.getObjs_size());
        List<Concept_SE_ISI> candidates = new ArrayList<>();

        for (Concept_SE_ISI concept : concepts) {
            if (is_subset(universe, concept.getExtent())) {
                candidates.add(concept);
            }
        }

        List<Concept_SE_ISI> directSubConcepts = new ArrayList<>();
        for (Concept_SE_ISI candidate : candidates) {
            boolean hasMiddleConcept = false;
            for (Concept_SE_ISI middle : candidates) {
                if (candidate == middle) {
                    continue;
                }
                if (is_subset(middle.getExtent(), candidate.getExtent())
                        && is_subset(universe, middle.getExtent())) {
                    hasMiddleConcept = true;
                    break;
                }
            }
            if (!hasMiddleConcept) {
                directSubConcepts.add(candidate);
            }
        }

        return directSubConcepts;
    }
}
