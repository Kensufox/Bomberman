package com.game.utils;

import javafx.scene.input.KeyCode;

import java.io.*;
import java.util.Properties;

/**
 * Handles loading, saving, and managing player input configurations for a game.
 * 
 * <p>Supports reading key bindings for two players from a properties file,
 * converting them to {@link KeyCode}, providing getters/setters,
 * resetting to defaults, and saving updated configurations.</p>
 */
public class InputHandler {
    private Properties gameProperties;

    private static final String CONFIG_FILE = "config.properties";

    private KeyCode j1Up, j1Down, j1Left, j1Right, j1Bomb;
    private KeyCode j2Up, j2Down, j2Left, j2Right, j2Bomb;

    /**
     * Constructs an InputHandler, loading configuration from file or
     * falling back to default key bindings if loading fails.
     */
    public InputHandler() {
        loadConfiguration();
        convertStringKeysToKeyCodes();
    }

    /**
     * Loads key bindings from the properties configuration file.
     * If the file cannot be found or an error occurs, resets to default keys.
     */
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

    /**
     * Converts string representations of key names from the loaded properties
     * into {@link KeyCode} objects.
     * If a key is missing or invalid, sets the corresponding key to a default value.
     */
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

        // Set defaults if any key is null
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
     * Converts a string representation of a key to a {@link KeyCode}.
     * Supports special names like "space", "up", "down", "left", "right", and "control".
     * 
     * @param keyString the string representation of the key
     * @return the corresponding KeyCode, or null if the string is invalid or empty
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
     * Converts a {@link KeyCode} to its string representation suitable for saving.
     * 
     * @param key the KeyCode to convert
     * @return the string representation of the key, or an empty string if key is null
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

    /**
     * Encapsulates the key controls for a single player.
     */
    public static class PlayerControls {
        /** KeyCodes for moving up, down, left, right, and placing a bomb respectively. */
        public final KeyCode up, down, left, right, bomb;

        /**
         * Creates a new PlayerControls instance.
         * 
         * @param up key for moving up
         * @param down key for moving down
         * @param left key for moving left
         * @param right key for moving right
         * @param bomb key for placing a bomb
         */
        public PlayerControls(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.bomb = bomb;
        }
    }

    /**
     * Returns the controls for player 1.
     * 
     * @return a PlayerControls object containing player 1's key bindings
     */
    public PlayerControls getJ1Controls() {
        return new PlayerControls(j1Up, j1Down, j1Left, j1Right, j1Bomb);
    }

    /**
     * Returns the controls for player 2.
     * 
     * @return a PlayerControls object containing player 2's key bindings
     */
    public PlayerControls getJ2Controls() {
        return new PlayerControls(j2Up, j2Down, j2Left, j2Right, j2Bomb);
    }

    // Setters for Player 1's key bindings

    /**
     * Sets the key used for Player 1's "move up" action.
     * @param key the KeyCode to assign to Player 1's up control
     */
    public void setJ1Up(KeyCode key) { this.j1Up = key; }

    /**
     * Sets the key used for Player 1's "move down" action.
     * @param key the KeyCode to assign to Player 1's down control
     */
    public void setJ1Down(KeyCode key) { this.j1Down = key; }

    /**
     * Sets the key used for Player 1's "move left" action.
     * @param key the KeyCode to assign to Player 1's left control
     */
    public void setJ1Left(KeyCode key) { this.j1Left = key; }

    /**
     * Sets the key used for Player 1's "move right" action.
     * @param key the KeyCode to assign to Player 1's right control
     */
    public void setJ1Right(KeyCode key) { this.j1Right = key; }

    /**
     * Sets the key used for Player 1's "place bomb" action.
     * @param key the KeyCode to assign to Player 1's bomb control
     */
    public void setJ1Bomb(KeyCode key) { this.j1Bomb = key; }


    // Setters for Player 2's key bindings

    /**
     * Sets the key used for Player 2's "move up" action.
     * @param key the KeyCode to assign to Player 2's up control
     */
    public void setJ2Up(KeyCode key) { this.j2Up = key; }

    /**
     * Sets the key used for Player 2's "move down" action.
     * @param key the KeyCode to assign to Player 2's down control
     */
    public void setJ2Down(KeyCode key) { this.j2Down = key; }

    /**
     * Sets the key used for Player 2's "move left" action.
     * @param key the KeyCode to assign to Player 2's left control
     */
    public void setJ2Left(KeyCode key) { this.j2Left = key; }

    /**
     * Sets the key used for Player 2's "move right" action.
     * @param key the KeyCode to assign to Player 2's right control
     */
    public void setJ2Right(KeyCode key) { this.j2Right = key; }

    /**
     * Sets the key used for Player 2's "place bomb" action.
     * @param key the KeyCode to assign to Player 2's bomb control
     */
    public void setJ2Bomb(KeyCode key) { this.j2Bomb = key; }


    /**
     * Returns the key assigned for Player 1 to move up.
     * @return the KeyCode for Player 1's up movement
     */
    public KeyCode getJ1Up() { return j1Up; }

    /**
     * Returns the key assigned for Player 1 to move down.
     * @return the KeyCode for Player 1's down movement
     */
    public KeyCode getJ1Down() { return j1Down; }

    /**
     * Returns the key assigned for Player 1 to move left.
     * @return the KeyCode for Player 1's left movement
     */
    public KeyCode getJ1Left() { return j1Left; }

    /**
     * Returns the key assigned for Player 1 to move right.
     * @return the KeyCode for Player 1's right movement
     */
    public KeyCode getJ1Right() { return j1Right; }

    /**
     * Returns the key assigned for Player 1 to place a bomb.
     * @return the KeyCode for Player 1's bomb action
     */
    public KeyCode getJ1Bomb() { return j1Bomb; }

    /**
     * Returns the key assigned for Player 2 to move up.
     * @return the KeyCode for Player 2's up movement
     */
    public KeyCode getJ2Up() { return j2Up; }

    /**
     * Returns the key assigned for Player 2 to move down.
     * @return the KeyCode for Player 2's down movement
     */
    public KeyCode getJ2Down() { return j2Down; }

    /**
     * Returns the key assigned for Player 2 to move left.
     * @return the KeyCode for Player 2's left movement
     */
    public KeyCode getJ2Left() { return j2Left; }

    /**
     * Returns the key assigned for Player 2 to move right.
     * @return the KeyCode for Player 2's right movement
     */
    public KeyCode getJ2Right() { return j2Right; }

    /**
     * Returns the key assigned for Player 2 to place a bomb.
     * @return the KeyCode for Player 2's bomb action
     */
    public KeyCode getJ2Bomb() { return j2Bomb; }

    /**
     * Resets all player keys to their default values.
     */
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

    /**
     * Saves the current key bindings to the configuration file.
     * If saving fails, an error message is printed.
     */
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
