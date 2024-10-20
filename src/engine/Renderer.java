package engine;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Renderer extends JPanel {
    float[] vertices;
    float[] textures;
    int[][] faces;
    Random random;

    public Renderer(Object[] objData) {
        this.vertices = (float[]) objData[0];
        this.textures = (float[]) objData[1];
        this.faces = (int[][]) objData[2];
        this.random = new Random();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int[][] projPoints = proj2d();

        // Prepare an array to hold the depth of each triangle and its index
        double[][] triangleDepths = new double[faces.length][2];

        for (int i = 0; i < faces.length; i++) {
            int v0 = faces[i][0];
            int v1 = faces[i][1];
            int v2 = faces[i][2];

            // Replace the depth calculation in the loop

            float avgX = ((vertices[v0 * 3] + vertices[v1 * 3] + vertices[v2 * 3]) / 3.0f) - Camera.pos.x;
            float avgY = ((vertices[v0 * 3 + 1] + vertices[v1 * 3 + 1] + vertices[v2 * 3 + 1]) / 3.0f) - Camera.pos.y;

            float avgZ = ((vertices[v0 * 3 + 2] + vertices[v1 * 3 + 2] + vertices[v2 * 3 + 2]) / 3.0f) - Camera.pos.z;
            triangleDepths[i][0] = Math.sqrt(Math.pow(avgX, 2) + Math.pow(avgZ, 2) + Math.pow(avgY, 2));

            triangleDepths[i][1] = i;  // Store the index of the face
        }

        // Sort the triangles by depth (ascending order)

        java.util.Arrays.sort(triangleDepths, (a, b) -> Double.compare(b[0], a[0]));

        // Draw the triangles in sorted order
        // Draw the triangles in sorted order
        for (double[] depthInfo : triangleDepths) {
            int i = (int) depthInfo[1];  // Retrieve the face index

            int v0 = faces[i][0];
            int v1 = faces[i][1];
            int v2 = faces[i][2];
            if (isTriangleVisible(v0, v1, v2, projPoints)) {
                float maxDist = 100;
                float light;
                float depth = (float) depthInfo[0];  // Correct depth reference
                if (depth > maxDist) {
                    depth = maxDist;
                }
                if (depth >= maxDist) {
                    light = 0;
                } else {
                    light = 1 - depth / maxDist;
                }



                float lightScale = 2.5f * light;
                int rDef = 10;
                int gDef = 20;
                int bDef = 30;
                int rVal = Math.min(200, (int) (rDef * lightScale)+(int)(rDef*.5f));
                int gVal = Math.min(200, (int) (gDef * lightScale)+(int)(gDef*.5f));
                int bVal = Math.min(200, (int) (bDef * lightScale)+(int)(bDef*.5f));
                Color lightedColor = new Color(rVal, gVal, bVal);
                g.setColor(lightedColor);

                int[] triX = {projPoints[v0][0], projPoints[v1][0], projPoints[v2][0]};
                int[] triY = {projPoints[v0][1], projPoints[v1][1], projPoints[v2][1]};
                g.fillPolygon(new Polygon(triX, triY, 3));
            }
        }

        Camera.updateFocalLength(getHeight());
    }


    private boolean isTriangleVisible(int v0, int v1, int v2, int[][] projPoints) {
        if (projPoints[v0][0] == 0 && projPoints[v0][1] == 0 || projPoints[v1][0] == 0 && projPoints[v1][1] == 0 || projPoints[v2][0] == 0 && projPoints[v2][1] == 0) {
            return false;
        } else {
            return true;
        }
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
}
