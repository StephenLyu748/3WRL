package utils.item;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LabelObjectRepository {
    private final Map<String, int[]> labelObjects = new HashMap<>();

    public static LabelObjectRepository ressZhenLabels() {
        LabelObjectRepository repository = new LabelObjectRepository();
        repository.put("d1", 1, 2, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15, 16, 17, 18, 19, 20);
        repository.put("d2", 1, 5, 6, 9, 11, 13, 14, 15, 17, 18, 19, 20);
        repository.put("d3", 3, 7, 10, 11);
        return repository;
    }

    public void put(String label, int... originalObjectIds) {
        labelObjects.put(normalize(label), originalObjectIds);
    }

    public BitSet getObjectsForLabels(String labelExpression) {
        BitSet objects = null;
        String[] labels = labelExpression.replace("\"", " ")
                .replace("'", " ")
                .split("[,\\s]+");
        for (String label : labels) {
            if (label.trim().isEmpty()) {
                continue;
            }
            int[] objectIds = labelObjects.get(normalize(label));
            if (objectIds == null) {
                throw new IllegalArgumentException("Unknown label: " + label);
            }
            BitSet labelObjectsSet = new BitSet();
            for (int objectId : objectIds) {
                labelObjectsSet.set(objectId);
            }
            if (objects == null) {
                objects = labelObjectsSet;
            } else {
                objects.and(labelObjectsSet);
            }
        }
        return objects == null ? new BitSet() : objects;
    }

    private static String normalize(String label) {
        return label.trim().toLowerCase(Locale.ROOT);
    }
}
