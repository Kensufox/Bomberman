package com.game.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private Properties gameProperties;

    private static final String CONFIG_FILE = "config.properties";

    private KeyCode j1Up, j1Down, j1Left, j1Right, j1Bomb;
    private KeyCode j2Up, j2Down, j2Left, j2Right, j2Bomb;

    // Constructeur
    public InputHandler() {
        loadConfiguration();
        convertStringKeysToKeyCodes();
    }

    public void loadConfiguration() {
        gameProperties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Impossible de trouver config.properties");
                resetToDefaults();
                return;
            }
            gameProperties.load(input);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            resetToDefaults();
        }
    }

    public void convertStringKeysToKeyCodes() {
        // Player 1
        String j1UpStr = gameProperties.getProperty("j1.up");
        String j1DownStr = gameProperties.getProperty("j1.down");
        String j1LeftStr = gameProperties.getProperty("j1.left");
        String j1RightStr = gameProperties.getProperty("j1.right");
        String j1BombStr = gameProperties.getProperty("j1.bomb");

        // Player 2
        String j2UpStr = gameProperties.getProperty("j2.up");
        String j2DownStr = gameProperties.getProperty("j2.down");
        String j2LeftStr = gameProperties.getProperty("j2.left");
        String j2RightStr = gameProperties.getProperty("j2.right");
        String j2BombStr = gameProperties.getProperty("j2.bomb");

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

        // Si une touche est nulle, on met une valeur par défaut
        if (j1Up == null) j1Up = KeyCode.Z;
        if (j1Down == null) j1Down = KeyCode.S;
        if (j1Left == null) j1Left = KeyCode.Q;
        if (j1Right == null) j1Right = KeyCode.D;
        if (j1Bomb == null) j1Bomb = KeyCode.SPACE;

        if (j2Up == null) j2Up = KeyCode.UP;
        if (j2Down == null) j2Down = KeyCode.DOWN;
        if (j2Left == null) j2Left = KeyCode.LEFT;
        if (j2Right == null) j2Right = KeyCode.RIGHT;
        if (j2Bomb == null) j2Bomb = KeyCode.CONTROL;
    }

    /** 
     * @param keyString
     * @return KeyCode
     */
    public KeyCode stringToKeyCode(String keyString) {
        if (keyString == null || keyString.isEmpty()) return null;
        try {
            switch (keyString.toLowerCase()) {
                case "space": return KeyCode.SPACE;
                case "up": return KeyCode.UP;
                case "down": return KeyCode.DOWN;
                case "left": return KeyCode.LEFT;
                case "right": return KeyCode.RIGHT;
                case "control": return KeyCode.CONTROL;
                default:
                    return KeyCode.valueOf(keyString.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Touche invalide: " + keyString);
            return null;
        }
    }

    /** 
     * @param key
     * @return String
     */
    private String keyCodeToString(KeyCode key) {
        if (key == null) return "";
        switch (key) {
            case SPACE: return "SPACE";
            case UP: return "UP";
            case DOWN: return "DOWN";
            case LEFT: return "LEFT";
            case RIGHT: return "RIGHT";
            case CONTROL: return "CONTROL";
            default: return key.getName().toUpperCase();
        }
    }

    // Classe interne pour regrouper les contrôles d'un joueur
    public static class PlayerControls {
        public final KeyCode up, down, left, right, bomb;
        public PlayerControls(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.bomb = bomb;
        }
    }

    /** 
     * @return PlayerControls
     */
    // Getters groupés
    public PlayerControls getJ1Controls() {
        return new PlayerControls(j1Up, j1Down, j1Left, j1Right, j1Bomb);
    }

    /** 
     * @return PlayerControls
     */
    public PlayerControls getJ2Controls() {
        return new PlayerControls(j2Up, j2Down, j2Left, j2Right, j2Bomb);
    }

    /** 
     * @param resetToDefaults(
     */
    // Setters individuels
    public void setJ1Up(KeyCode key) { this.j1Up = key; }
    public void setJ1Down(KeyCode key) { this.j1Down = key; }
    public void setJ1Left(KeyCode key) { this.j1Left = key; }
    public void setJ1Right(KeyCode key) { this.j1Right = key; }
    public void setJ1Bomb(KeyCode key) { this.j1Bomb = key; }

    public void setJ2Up(KeyCode key) { this.j2Up = key; }
    public void setJ2Down(KeyCode key) { this.j2Down = key; }
    public void setJ2Left(KeyCode key) { this.j2Left = key; }
    public void setJ2Right(KeyCode key) { this.j2Right = key; }
    public void setJ2Bomb(KeyCode key) { this.j2Bomb = key; }

    /** 
     * @param resetToDefaults(
     * @return KeyCode
     */
    // Getters individuels
    public KeyCode getJ1Up() { return j1Up; }
    public KeyCode getJ1Down() { return j1Down; }
    public KeyCode getJ1Left() { return j1Left; }
    public KeyCode getJ1Right() { return j1Right; }
    public KeyCode getJ1Bomb() { return j1Bomb; }

    public KeyCode getJ2Up() { return j2Up; }
    public KeyCode getJ2Down() { return j2Down; }
    public KeyCode getJ2Left() { return j2Left; }
    public KeyCode getJ2Right() { return j2Right; }
    public KeyCode getJ2Bomb() { return j2Bomb; }

    public void resetToDefaults() {
        j1Up = KeyCode.Z;
        j1Down = KeyCode.S;
        j1Left = KeyCode.Q;
        j1Right = KeyCode.D;
        j1Bomb = KeyCode.SPACE;

        j2Up = KeyCode.UP;
        j2Down = KeyCode.DOWN;
        j2Left = KeyCode.LEFT;
        j2Right = KeyCode.RIGHT;
        j2Bomb = KeyCode.CONTROL;
    }

    public void saveSettings() {
        if (gameProperties == null) gameProperties = new Properties();

        gameProperties.setProperty("j1.up", keyCodeToString(j1Up));
        gameProperties.setProperty("j1.down", keyCodeToString(j1Down));
        gameProperties.setProperty("j1.left", keyCodeToString(j1Left));
        gameProperties.setProperty("j1.right", keyCodeToString(j1Right));
        gameProperties.setProperty("j1.bomb", keyCodeToString(j1Bomb));

        gameProperties.setProperty("j2.up", keyCodeToString(j2Up));
        gameProperties.setProperty("j2.down", keyCodeToString(j2Down));
        gameProperties.setProperty("j2.left", keyCodeToString(j2Left));
        gameProperties.setProperty("j2.right", keyCodeToString(j2Right));
        gameProperties.setProperty("j2.bomb", keyCodeToString(j2Bomb));

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            gameProperties.store(output, "Game controls configuration");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des paramètres: " + e.getMessage());
        }
    }
}
