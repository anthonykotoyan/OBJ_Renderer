package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
    public static float[] loadVertices(String path) throws IOException {
        List<Float> vertices = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();  // Remove leading/trailing spaces
            if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");  // Split by any whitespace
                try {
                    vertices.add(Float.parseFloat(parts[1]));
                    vertices.add(Float.parseFloat(parts[2]));
                    vertices.add(Float.parseFloat(parts[3]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        }
        reader.close();

        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        return vertexArray;
    }
}
