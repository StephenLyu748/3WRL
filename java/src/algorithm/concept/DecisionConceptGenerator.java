package algorithm.concept;

import utils.Context;
import utils.concept.Concept;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** Generates all ordinary formal concepts of a complete decision context. */
public final class DecisionConceptGenerator {
    private DecisionConceptGenerator() {
    }

    public static List<Concept> generate(Context context) {
        validate(context);

        List<Concept> concepts = new ArrayList<>();
        BitSet intent = closure(context, new BitSet());

        while (intent != null) {
            BitSet extent = deriveExtent(context, intent);
            concepts.add(new Concept(extent, (BitSet) intent.clone()));
            intent = nextClosure(context, intent);
        }
        return concepts;
    }

    /** C' = {x in U | every decision attribute in C is owned by x}. */
    public static BitSet deriveExtent(Context context, BitSet intent) {
        BitSet extent = new BitSet();
        extent.set(1, context.getObjs_size() + 1);

        for (int attribute = intent.nextSetBit(1);
             attribute >= 0;
             attribute = intent.nextSetBit(attribute + 1)) {
            BitSet attributeExtent = context.getAttrs().get(attribute);
            if (attributeExtent == null) {
                throw new IllegalArgumentException("Unknown decision attribute: " + attribute);
            }
            extent.and(attributeExtent);
        }
        return extent;
    }

    /** Y' = {d in D | every object in Y owns d}. */
    public static BitSet deriveIntent(Context context, BitSet extent) {
        BitSet intent = new BitSet();
        intent.set(1, context.getAttrs_size() + 1);

        for (int object = extent.nextSetBit(1);
             object >= 0;
             object = extent.nextSetBit(object + 1)) {
            BitSet objectIntent = context.getObjs().get(object);
            if (objectIntent == null) {
                throw new IllegalArgumentException("Unknown object: " + object);
            }
            intent.and(objectIntent);
        }
        return intent;
    }

    public static BitSet closure(Context context, BitSet intent) {
        return deriveIntent(context, deriveExtent(context, intent));
    }

    private static BitSet nextClosure(Context context, BitSet current) {
        int attributeCount = context.getAttrs_size();
        for (int attribute = attributeCount; attribute >= 1; attribute--) {
            if (current.get(attribute)) {
                continue;
            }

            BitSet seed = (BitSet) current.clone();
            seed.clear(attribute, attributeCount + 1);
            seed.set(attribute);
            BitSet candidate = closure(context, seed);

            if (hasSamePrefix(current, candidate, attribute)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean hasSamePrefix(BitSet left, BitSet right, int endExclusive) {
        for (int attribute = 1; attribute < endExclusive; attribute++) {
            if (left.get(attribute) != right.get(attribute)) {
                return false;
            }
        }
        return true;
    }

    private static void validate(Context context) {
        if (context == null || context.getObjs() == null || context.getAttrs() == null) {
            throw new IllegalArgumentException("A complete decision context is required");
        }
        if (context.getObjs_size() <= 0 || context.getAttrs_size() <= 0) {
            throw new IllegalArgumentException("Decision context dimensions must be positive");
        }
    }
}
