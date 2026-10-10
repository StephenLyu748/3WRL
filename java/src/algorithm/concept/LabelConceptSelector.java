package algorithm.concept;

import utils.concept.Concept_SE_ISI;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

public class LabelConceptSelector {
    public static List<Concept_SE_ISI> findSubConceptsContainedInLabel(
            Collection<Concept_SE_ISI> concepts,
            BitSet labelExtent
    ) {
        List<Concept_SE_ISI> result = new ArrayList<>();
        for (Concept_SE_ISI concept : concepts) {
            if (isSubset(concept.getExtent(), labelExtent)) {
                result.add(concept);
            }
        }
        return result;
    }

    public static List<Concept_SE_ISI> findProperSubConceptsContainedInLabel(
            Collection<Concept_SE_ISI> concepts,
            BitSet labelExtent
    ) {
        List<Concept_SE_ISI> result = new ArrayList<>();
        for (Concept_SE_ISI concept : concepts) {
            if (isProperSubset(concept.getExtent(), labelExtent)) {
                result.add(concept);
            }
        }
        return result;
    }

    public static List<Concept_SE_ISI> findMaximalExtentConcepts(
            Collection<Concept_SE_ISI> concepts
    ) {
        List<Concept_SE_ISI> maximalConcepts = new ArrayList<>();
        for (Concept_SE_ISI candidate : concepts) {
            boolean hasLargerConcept = false;
            for (Concept_SE_ISI other : concepts) {
                if (candidate == other) {
                    continue;
                }
                if (isProperSubset(candidate.getExtent(), other.getExtent())) {
                    hasLargerConcept = true;
                    break;
                }
            }
            if (!hasLargerConcept) {
                maximalConcepts.add(candidate);
            }
        }
        return maximalConcepts;
    }

    public static List<Concept_SE_ISI> findDirectSubConceptsOfLabel(
            Collection<Concept_SE_ISI> concepts,
            BitSet labelExtent
    ) {
        List<Concept_SE_ISI> candidates =
                findProperSubConceptsContainedInLabel(concepts, labelExtent);

        List<Concept_SE_ISI> directSubConcepts = new ArrayList<>();
        for (Concept_SE_ISI candidate : candidates) {
            boolean hasMiddleConcept = false;
            for (Concept_SE_ISI middle : candidates) {
                if (candidate == middle) {
                    continue;
                }
                if (isProperSubset(candidate.getExtent(), middle.getExtent())
                        && isProperSubset(middle.getExtent(), labelExtent)) {
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

    public static List<Concept_SE_ISI> findDirectParentConcepts(
            Collection<Concept_SE_ISI> concepts,
            Concept_SE_ISI childConcept
    ) {
        return findDirectParentConcepts(concepts, childConcept.getExtent());
    }

    public static List<Concept_SE_ISI> findDirectParentConcepts(
            Collection<Concept_SE_ISI> concepts,
            BitSet childExtent
    ) {
        List<Concept_SE_ISI> parentCandidates = new ArrayList<>();
        for (Concept_SE_ISI concept : concepts) {
            if (isProperSubset(childExtent, concept.getExtent())) {
                parentCandidates.add(concept);
            }
        }

        List<Concept_SE_ISI> directParents = new ArrayList<>();
        for (Concept_SE_ISI candidateParent : parentCandidates) {
            boolean hasMiddleConcept = false;
            for (Concept_SE_ISI middle : parentCandidates) {
                if (candidateParent == middle) {
                    continue;
                }
                if (isProperSubset(childExtent, middle.getExtent())
                        && isProperSubset(middle.getExtent(), candidateParent.getExtent())) {
                    hasMiddleConcept = true;
                    break;
                }
            }
            if (!hasMiddleConcept) {
                directParents.add(candidateParent);
            }
        }

        return directParents;
    }

    public static BitSet makeObjectSet(int... objectIds) {
        BitSet objectSet = new BitSet();
        for (int objectId : objectIds) {
            objectSet.set(objectId);
        }
        return objectSet;
    }

    private static boolean isSubset(BitSet subset, BitSet superset) {
        BitSet remaining = (BitSet) subset.clone();
        remaining.andNot(superset);
        return remaining.isEmpty();
    }

    private static boolean isProperSubset(BitSet subset, BitSet superset) {
        return !subset.equals(superset) && isSubset(subset, superset);
    }
}
