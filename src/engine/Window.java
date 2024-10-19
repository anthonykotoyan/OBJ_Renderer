package engine;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    public Window(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
    }

    public void start() {
        setVisible(true);
    }
}
