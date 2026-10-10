package utils.readFile;

import utils.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/** Reads a complete binary decision context. */
public final class DecisionContextReader {
    private DecisionContextReader() {
    }

    public static Context readFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Decision context file is empty: " + filename);
            }

            String[] dimensions = splitRow(header);
            if (dimensions.length != 2) {
                throw new IOException("The first line must be: objectCount,decisionAttributeCount");
            }

            int objectCount = parsePositiveInt(dimensions[0], "object count");
            int attributeCount = parsePositiveInt(dimensions[1], "decision attribute count");
            Map<Integer, BitSet> objects = new HashMap<>();
            Map<Integer, BitSet> attributes = new HashMap<>();

            for (int attribute = 1; attribute <= attributeCount; attribute++) {
                attributes.put(attribute, new BitSet());
            }

            for (int object = 1; object <= objectCount; object++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Missing data row for object " + object);
                }

                String[] values = splitRow(line);
                if (values.length != attributeCount) {
                    throw new IOException("Object " + object + " has " + values.length
                            + " values; expected " + attributeCount);
                }

                BitSet objectIntent = new BitSet();
                for (int column = 0; column < attributeCount; column++) {
                    String value = values[column];
                    if (!"0".equals(value) && !"1".equals(value)) {
                        throw new IOException("Object " + object + ", decision attribute "
                                + (column + 1) + " must be 0 or 1, but was: " + value);
                    }
                    if ("1".equals(value)) {
                        int attribute = column + 1;
                        objectIntent.set(attribute);
                        attributes.get(attribute).set(object);
                    }
                }
                objects.put(object, objectIntent);
            }

            String extraLine;
            while ((extraLine = reader.readLine()) != null) {
                if (!extraLine.trim().isEmpty()) {
                    throw new IOException("Unexpected extra data after object " + objectCount);
                }
            }

            Context context = new Context();
            context.setObjs_size(objectCount);
            context.setAttrs_size(attributeCount);
            context.setObjs(objects);
            context.setAttrs(attributes);
            return context;
        }
    }

    private static String[] splitRow(String line) {
        String[] values = line.trim().split(",", -1);
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
        return values;
    }

    private static int parsePositiveInt(String value, String name) throws IOException {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new IOException(name + " must be greater than zero");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IOException("Invalid " + name + ": " + value, e);
        }
    }
}
