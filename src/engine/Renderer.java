package engine;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Renderer extends JPanel {
    float[] vertices;
    float[] textures;
    int[][] faces;
    private static final Random random = new Random();
    static Color[] randColor;



    public Renderer(Object[] objData) {
        this.vertices = (float[]) objData[0];
        this.textures = (float[]) objData[1];
        this.faces = (int[][]) objData[2];
        randColor = new Color[faces.length];


        for (int i = 0; i < randColor.length; i++){
            int randomFactor = 10;
            int red = getRandomInRange(-randomFactor, randomFactor);
            int green = getRandomInRange(-randomFactor, randomFactor);
            int blue = getRandomInRange(-randomFactor, randomFactor);

            Color colorPickMode = new Color(100, 100, 200); // this is only for the color picker in IntelliJ

            randColor[i] = new Color(colorPickMode.getRed()+red, colorPickMode.getGreen()+green, colorPickMode.getBlue()+blue);

        }
    }
    private static int getRandomInRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int[][] projPoints = proj2d();

        // Prepare an array to hold the depth of each triangle and its index
        double[][] polygonDepths = new double[faces.length][2];

        for (int i = 0; i < faces.length; i++) {

            float avgX = 0;
            float avgY = 0;
            float avgZ = 0;
            for (int j = 0; j < faces[i].length; j++) {
                int v = faces[i][j];
                avgX += vertices[v * 3];
                avgY += vertices[v * 3 + 1];
                avgZ += vertices[v * 3 + 2];
            }


            avgX = avgX / faces[i].length - Camera.pos.x;
            avgY = avgY / faces[i].length - Camera.pos.y;
            avgZ = avgZ / faces[i].length - Camera.pos.z;

            polygonDepths[i][0] = Math.sqrt(Math.pow(avgX, 2) + Math.pow(avgZ, 2) + Math.pow(avgY, 2));

            polygonDepths[i][1] = i;  // Store the index of the face
        }

        // Sort the triangles by depth (ascending order)

        java.util.Arrays.sort(polygonDepths, (a, b) -> Double.compare(b[0], a[0]));

        // Draw the triangles in sorted order
        // Draw the triangles in sorted order
        for (double[] depthInfo : polygonDepths) {
            int i = (int) depthInfo[1];  // Retrieve the face index


            if (!isFaceClipping(faces[i], projPoints, i)) {
                Color lightedColor = calcLighting(depthInfo, i);
                g.setColor(lightedColor);

                int[] polyX = new int[faces[i].length];
                int[] polyY = new int[faces[i].length];
                for (int j = 0; j < faces[i].length; j++) {
                    polyX[j] = projPoints[faces[i][j]][0];
                    polyY[j] = projPoints[faces[i][j]][1];
                }


                g.fillPolygon(new Polygon(polyX, polyY, faces[i].length));
            }
        }

        Camera.updateFocalLength(getHeight());
    }




    private boolean isFaceClipping(int[] face, int[][] projPoints, int currFace) {
        
        for (int i = 0; i < face.length; i++){
            if (projPoints[face[i]][0] == 0 && projPoints[face[i]][1] == 0){
                return true;
            }
        }
        return false;
    }

    private int[][] proj2d() {

        int[][] projPoints = new int[(int) vertices.length * 1 / 3][2];
        float rotX = Camera.rot.x;
        float rotY = Camera.rot.y;


        for (int i = 0; i < vertices.length; i += 3) {

            Vector3f vertex = new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]);


            // Translate the vertex to the origin based on the camera position
            vertex.x -= Camera.pos.x;
            vertex.y -= Camera.pos.y;
            vertex.z -= Camera.pos.z;

            // Apply rotation around the camera position
            vertex = rotate(vertex, rotX, rotY);

            // Translate the vertex back to the camera position
            vertex.x += Camera.pos.x;
            vertex.y += Camera.pos.y;
            vertex.z += Camera.pos.z;


            float relX = vertex.x - Camera.pos.x;
            float relY = vertex.y - Camera.pos.y;
            float relZ = vertex.z - Camera.pos.z;


            if (relZ <= 0) {
                continue;  // Skip rendering this vertex
            }

            // Project the 3D point to 2D
            float projX = (relX / relZ) * Camera.focalLength;
            float projY = -(relY / relZ) * Camera.focalLength;

            // Center the projection in the panel
            projX += getWidth() / 2;
            projY += getHeight() / 2;
            projPoints[(int) (i / 3)] = new int[]{(int) projX, (int) projY};
        }
        return projPoints;


    }

    private Vector3f rotate(Vector3f v, float rotX, float rotY) {
        float cosY = (float) Math.cos(rotY);
        float sinY = (float) Math.sin(rotY);

        // Rotate around Y-axis (yaw)
        float newX = v.x * cosY - v.z * sinY;
        float newZ = v.x * sinY + v.z * cosY;

        // Now we can apply pitch (rotX) to the rotated vector
        // Update the y-component based on pitch
        float cosX = (float) Math.cos(rotX);
        float sinX = (float) Math.sin(rotX);

        float newY = newZ * sinX + v.y * cosX;
        newZ = newZ * cosX - v.y * sinX;

        // Create and return the new rotated vector
        return new Vector3f(newX, newY, newZ);
    }

    private static Color calcLighting(double[] depthInfo, int currFace) {
        float maxDist = 100;
        float light;
        float depth = (float) depthInfo[0];
        float minLight = .025f;

        if (depth > maxDist) {
            depth = maxDist;
        }
        if (depth >= maxDist) {
            light = minLight;
        } else {
            light = 1 - depth / maxDist;
        }


        float lightScale = light;

        int rDef = randColor[currFace].getRed();
        int gDef = randColor[currFace].getGreen();
        int bDef = randColor[currFace].getBlue();
        int rVal = Math.min(200, (int) (rDef * lightScale));
        int gVal = Math.min(200, (int) (gDef * lightScale));
        int bVal = Math.min(200, (int) (bDef * lightScale));

        return new Color(rVal, gVal, bVal);
    }
}
