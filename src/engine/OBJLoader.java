package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {
    public static Object[] loadOBJ(String path, float scale) throws IOException {
        List<Float> vertices = new ArrayList<>();
        List<Float> vertexTextures = new ArrayList<>();
        List<int[]> faces = new ArrayList<>(); // To store face indices

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();  // Remove leading/trailing spaces

            // Read vertices
            if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");  // Split by whitespace
                try {
                    vertices.add(Float.parseFloat(parts[1]));
                    vertices.add(Float.parseFloat(parts[2]));
                    vertices.add(Float.parseFloat(parts[3]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed vertex line: " + line);
                }
            }

            // Read texture coordinates
            if (line.startsWith("vt ")) {
                String[] parts = line.split("\\s+");
                try {
                    vertexTextures.add(Float.parseFloat(parts[1]));
                    vertexTextures.add(Float.parseFloat(parts[2]));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed texture line: " + line);
                }
            }

            // Read faces
            if (line.startsWith("f ")) {
                String[] parts = line.split("\\s+");
                int[] faceIndices = new int[parts.length - 1];  // Prepare to store indices
                for (int i = 1; i < parts.length; i++) {
                    String[] indices = parts[i].split("/");  // Split by '/'
                    try {
                        // The vertex index is the first number (1-based index)
                        faceIndices[i - 1] = Integer.parseInt(indices[0]) - 1; // Convert to 0-based index
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed face index: " + parts[i]);
                    }
                }
                faces.add(faceIndices);  // Add the face indices to the list
            }
        }
        reader.close();

        // Convert lists to arrays
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {

            vertexArray[i] = vertices.get(i)*scale;
        }
        float[] vertexTexturesArray = new float[vertexTextures.size()];
        for (int i = 0; i < vertexTextures.size(); i++) {
            vertexTexturesArray[i] = vertexTextures.get(i);
        }

        // Convert faces list to array
        int[][] faceArray = new int[faces.size()][];
        for (int i = 0; i < faces.size(); i++) {
            faceArray[i] = faces.get(i);
        }

        return new Object[] { vertexArray, vertexTexturesArray, faceArray };
    }
}
