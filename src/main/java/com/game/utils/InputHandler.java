package com.game.utils;

import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InputHandler {
    private Properties gameProperties;

    private KeyCode j1Up, j1Down, j1Left, j1Right, j1Bomb;
    private KeyCode j2Up, j2Down, j2Left, j2Right, j2Bomb;

    // Constructor
    public InputHandler() {
        loadConfiguration();
        convertStringKeysToKeyCodes();
    }

    // Method load config.properties
    private void loadConfiguration() {
        gameProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Impossible de trouver config.properties");
                return;
            }
            gameProperties.load(input);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
        }
    }

    private void convertStringKeysToKeyCodes() {
        // Player 1
        String j1UpStr = gameProperties.getProperty("controlsJ1.movement.up");
        String j1DownStr = gameProperties.getProperty("controlsJ1.movement.down");
        String j1LeftStr = gameProperties.getProperty("controlsJ1.movement.left");
        String j1RightStr = gameProperties.getProperty("controlsJ1.movement.right");
        String j1BombStr = gameProperties.getProperty("controlsJ1.action.bomb");

        // Player 2
        String j2UpStr = gameProperties.getProperty("controlsJ2.movement.up");
        String j2DownStr = gameProperties.getProperty("controlsJ2.movement.down");
        String j2LeftStr = gameProperties.getProperty("controlsJ2.movement.left");
        String j2RightStr = gameProperties.getProperty("controlsJ2.movement.right");
        String j2BombStr = gameProperties.getProperty("controlsJ2.action.bomb");

        // Conversion to key codes
        j1Up = stringToKeyCode(j1UpStr);
        j1Down = stringToKeyCode(j1DownStr);
        j1Left = stringToKeyCode(j1LeftStr);
        j1Right = stringToKeyCode(j1RightStr);
        j1Bomb = stringToKeyCode(j1BombStr);

        j2Up = stringToKeyCode(j2UpStr);
        j2Down = stringToKeyCode(j2DownStr);
        j2Left = stringToKeyCode(j2LeftStr);
        j2Right = stringToKeyCode(j2RightStr);
        j2Bomb = stringToKeyCode(j2BombStr);
    }

    // String to KeyCode conversion method
    public KeyCode stringToKeyCode(String keyString) {
        if (keyString == null) return null;

        try {
            // Managing special cases
            switch (keyString.toLowerCase()) {
                case "space": return KeyCode.SPACE;
                case "up": return KeyCode.UP;
                case "down": return KeyCode.DOWN;
                case "left": return KeyCode.LEFT;
                case "right": return KeyCode.RIGHT;
                default:
                    // For letters and other keys
                    return KeyCode.valueOf(keyString.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Touche invalide: " + keyString);
            return null;
        }
    }

    public KeyCode getJ2Bomb() {
        return j2Bomb;
    }

    public KeyCode getJ2Right() {
        return j2Right;
    }

    public KeyCode getJ2Left() {
        return j2Left;
    }

    public KeyCode getJ2Down() {
        return j2Down;
    }

    public KeyCode getJ2Up() {
        return j2Up;
    }

    public KeyCode getJ1Bomb() {
        return j1Bomb;
    }

    public KeyCode getJ1Right() {
        return j1Right;
    }

    public KeyCode getJ1Left() {
        return j1Left;
    }

    public KeyCode getJ1Down() {
        return j1Down;
    }

    public KeyCode getJ1Up() {
        return j1Up;
    }

    public void printConfiguration() {
        System.out.println("=== Configuration des contrôles ===");
        System.out.println("Joueur 1:");
        System.out.println("  Haut: " + getJ1Up());
        System.out.println("  Bas: " + getJ1Down());
        System.out.println("  Gauche: " + getJ1Left());
        System.out.println("  Droite: " + getJ1Right());
        System.out.println("  Bombe: " + getJ1Bomb());
        System.out.println("Joueur 2:");
        System.out.println("  Haut: " + getJ2Up());
        System.out.println("  Bas: " + getJ2Down());
        System.out.println("  Gauche: " + getJ2Left());
        System.out.println("  Droite: " + getJ2Right());
        System.out.println("  Bombe: " + getJ2Bomb());
    }
}