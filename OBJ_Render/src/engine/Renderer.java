package engine;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Renderer extends JPanel {
    private float[] vertices;

    public Renderer(float[] vertices) {
        this.vertices = vertices;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        for (int i = 0; i < vertices.length; i += 3) {
            int x = (int) (400 + vertices[i] * 100);  // Offset + scale
            int y = (int) (300 - vertices[i + 1] * 100);  // Invert Y axis

            g.fillOval(x, y, 5, 5);  // Draw a vertex
        }
    }
}
