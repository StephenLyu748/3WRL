package utils.item;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class ObjectIdMapper {
    private final Map<Integer, Integer> originalToInternal;
    private final Map<Integer, Integer> internalToOriginal;

    private ObjectIdMapper(
            Map<Integer, Integer> originalToInternal,
            Map<Integer, Integer> internalToOriginal
    ) {
        this.originalToInternal = originalToInternal;
        this.internalToOriginal = internalToOriginal;
    }

    public static ObjectIdMapper fromDeletedOriginalIds(int originalMaxId, int... deletedOriginalIds) {
        Arrays.sort(deletedOriginalIds);
        Map<Integer, Integer> originalToInternal = new HashMap<>();
        Map<Integer, Integer> internalToOriginal = new HashMap<>();

        int internalId = 1;
        for (int originalId = 1; originalId <= originalMaxId; originalId++) {
            if (contains(deletedOriginalIds, originalId)) {
                continue;
            }
            originalToInternal.put(originalId, internalId);
            internalToOriginal.put(internalId, originalId);
            internalId++;
        }

        return new ObjectIdMapper(originalToInternal, internalToOriginal);
    }

    public static ObjectIdMapper identity(int originalMaxId) {
        return fromDeletedOriginalIds(originalMaxId);
    }

    public static ObjectIdMapper fromOriginalOrder(int... originalIdsInInternalOrder) {
        Map<Integer, Integer> originalToInternal = new HashMap<>();
        Map<Integer, Integer> internalToOriginal = new HashMap<>();

        for (int i = 0; i < originalIdsInInternalOrder.length; i++) {
            int internalId = i + 1;
            int originalId = originalIdsInInternalOrder[i];
            originalToInternal.put(originalId, internalId);
            internalToOriginal.put(internalId, originalId);
        }

        return new ObjectIdMapper(originalToInternal, internalToOriginal);
    }

    public BitSet toInternalSet(BitSet originalSet) {
        BitSet internalSet = new BitSet();
        for (int originalId = originalSet.nextSetBit(0);
             originalId >= 0;
             originalId = originalSet.nextSetBit(originalId + 1)) {
            Integer internalId = originalToInternal.get(originalId);
            if (internalId != null) {
                internalSet.set(internalId);
            }
        }
        return internalSet;
    }

    public BitSet toOriginalSet(BitSet internalSet) {
        BitSet originalSet = new BitSet();
        for (int internalId = internalSet.nextSetBit(0);
             internalId >= 0;
             internalId = internalSet.nextSetBit(internalId + 1)) {
            Integer originalId = internalToOriginal.get(internalId);
            if (originalId != null) {
                originalSet.set(originalId);
            }
        }
        return originalSet;
    }

    public String formatOriginalSet(BitSet internalSet) {
        return toOriginalSet(internalSet).toString();
    }

    public boolean isDeletedOriginalId(int originalId) {
        return !originalToInternal.containsKey(originalId);
    }

    private static boolean contains(int[] values, int target) {
        for (int value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
