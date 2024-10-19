package engine;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Window window = new Window("3D Engine", 800, 600);

        float[] vertices = {};
        try {
            vertices = OBJLoader.loadVertices("src/models/male.obj");
        } catch (IOException e) {
            System.err.println("Failed to load OBJ file: " + e.getMessage());
        }

        Renderer renderer = new Renderer(vertices);
        window.add(renderer);
        window.start();
    }
}
