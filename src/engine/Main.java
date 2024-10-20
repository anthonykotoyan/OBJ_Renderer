package engine;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();

    public static void main(String[] args) {
        Window window = new Window("3D Engine", 1600, 900);

        Object[] objData = new Object[0];
        try {
            objData = OBJLoader.loadOBJ("src/models/skull.obj",.75f);
        } catch (IOException e) {
            System.err.println("Failed to load OBJ file: " + e.getMessage());
        }

        Renderer renderer = new Renderer(objData);
        window.add(renderer);
        window.start();

        JLabel label = new JLabel("Count: 0", SwingConstants.CENTER);
        window.getContentPane().add(label);

        // Initialize key states (default: not pressed)
        keyStates.put(KeyEvent.VK_W, false);
        keyStates.put(KeyEvent.VK_A, false);
        keyStates.put(KeyEvent.VK_S, false);
        keyStates.put(KeyEvent.VK_D, false);
        keyStates.put(KeyEvent.VK_SPACE, false);
        keyStates.put(KeyEvent.VK_SHIFT, false);
        keyStates.put(KeyEvent.VK_UP, false);
        keyStates.put(KeyEvent.VK_DOWN, false);
        keyStates.put(KeyEvent.VK_LEFT, false);
        keyStates.put(KeyEvent.VK_RIGHT, false);
        keyStates.put(KeyEvent.VK_Q, false);
        keyStates.put(KeyEvent.VK_E, false);

        // Add KeyListener to track key presses/releases
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyStates.put(e.getKeyCode(), true);  // Mark key as pressed
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyStates.put(e.getKeyCode(), false); // Mark key as released
            }
        });

        window.setFocusable(true);
        window.requestFocusInWindow();

        // Timer to update the game/render logic every frame
        Timer timer = new Timer(16, new ActionListener() {
            int count = 0;
            float speed = 2f;  // Speed of movement
            float rotSpeed = 0.025f;  // Speed of rotation

            @Override
            public void actionPerformed(ActionEvent e) {
                count++;
                label.setText("Count: " + count);

                // Calculate direction based on camera rotation
                float forwardX = (float) (Math.sin(Camera.rot.y) * speed);
                float forwardZ = (float) (Math.cos(Camera.rot.y) * speed);
                float rightX = (float) (Math.cos(Camera.rot.y) * speed);
                float rightZ = (float) (-Math.sin(Camera.rot.y) * speed);

                // Poll key states and perform actions
                if (keyStates.get(KeyEvent.VK_W)) { // Move forward
                    Camera.pos.x += forwardX;
                    Camera.pos.z += forwardZ;
                }
                if (keyStates.get(KeyEvent.VK_S)) { // Move backward
                    Camera.pos.x -= forwardX;
                    Camera.pos.z -= forwardZ;
                }
                if (keyStates.get(KeyEvent.VK_A)) { // Move left
                    Camera.pos.x -= rightX;
                    Camera.pos.z -= rightZ;
                }
                if (keyStates.get(KeyEvent.VK_D)) { // Move right
                    Camera.pos.x += rightX;
                    Camera.pos.z += rightZ;
                }
                if (keyStates.get(KeyEvent.VK_SPACE)) { // Move up
                    Camera.pos.y += speed;
                }
                if (keyStates.get(KeyEvent.VK_SHIFT)) { // Move down
                    Camera.pos.y -= speed;
                }
                if (keyStates.get(KeyEvent.VK_UP)) { // Rotate up
                    Camera.rot.x -= rotSpeed;
                }
                if (keyStates.get(KeyEvent.VK_DOWN)) { // Rotate down
                    Camera.rot.x += rotSpeed;
                }
                if (keyStates.get(KeyEvent.VK_LEFT)) { // Rotate left
                    Camera.rot.y -= rotSpeed;
                }
                if (keyStates.get(KeyEvent.VK_RIGHT)) { // Rotate right
                    Camera.rot.y += rotSpeed;
                }
                if (keyStates.get(KeyEvent.VK_Q)) { // Roll left (or counter-clockwise)
                    Camera.rot.z -= rotSpeed;
                }
                if (keyStates.get(KeyEvent.VK_E)) { // Roll right (or clockwise)
                    Camera.rot.z += rotSpeed;
                }

                window.repaint(); // Refresh display
            }
        });
        timer.start(); // Start the timer (game loop)
    }
}
