package com.game.controllers;

import javafx.scene.input.KeyCode;
import java.io.*;
import java.util.Properties;

public class GameSettings {
    private static GameSettings instance;
    private Properties properties;

    // Player 1 Controls
    private KeyCode player1UpKey = KeyCode.UP;
    private KeyCode player1DownKey = KeyCode.DOWN;
    private KeyCode player1LeftKey = KeyCode.LEFT;
    private KeyCode player1RightKey = KeyCode.RIGHT;
    private KeyCode player1BombKey = KeyCode.SPACE;

    // Player 2 Controls
    private KeyCode player2UpKey = KeyCode.W;
    private KeyCode player2DownKey = KeyCode.S;
    private KeyCode player2LeftKey = KeyCode.A;
    private KeyCode player2RightKey = KeyCode.D;
    private KeyCode player2BombKey = KeyCode.Q;

    private GameSettings() {
        properties = new Properties();
        loadSettings();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public void loadSettings() {
        try (InputStream input = new FileInputStream("/resources/config.properties")) {
            properties.load(input);

            // Load Player 1 controls
            player1UpKey = KeyCode.valueOf(properties.getProperty("player1UpKey", "Z"));
            player1DownKey = KeyCode.valueOf(properties.getProperty("player1DownKey", "S"));
            player1LeftKey = KeyCode.valueOf(properties.getProperty("player1LeftKey", "Q"));
            player1RightKey = KeyCode.valueOf(properties.getProperty("player1RightKey", "D"));
            player1BombKey = KeyCode.valueOf(properties.getProperty("player1BombKey", "SPACE"));

            // Load Player 2 controls
            player2UpKey = KeyCode.valueOf(properties.getProperty("player2UpKey", "UP"));
            player2DownKey = KeyCode.valueOf(properties.getProperty("player2DownKey", "DOWN"));
            player2LeftKey = KeyCode.valueOf(properties.getProperty("player2LeftKey", "LEFT"));
            player2RightKey = KeyCode.valueOf(properties.getProperty("player2RightKey", "RIGHT"));
            player2BombKey = KeyCode.valueOf(properties.getProperty("player2BombKey", "P"));

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Unable to load parameters, use default values");
            resetToDefaults();
        }
    }

    public void saveSettings() {
        try (OutputStream output = new FileOutputStream("/resources/config.properties")) {
            // Player 1 controls
            properties.setProperty("player1UpKey", player1UpKey.name());
            properties.setProperty("player1DownKey", player1DownKey.name());
            properties.setProperty("player1LeftKey", player1LeftKey.name());
            properties.setProperty("player1RightKey", player1RightKey.name());
            properties.setProperty("player1BombKey", player1BombKey.name());

            // Player 2 controls
            properties.setProperty("player2UpKey", player2UpKey.name());
            properties.setProperty("player2DownKey", player2DownKey.name());
            properties.setProperty("player2LeftKey", player2LeftKey.name());
            properties.setProperty("player2RightKey", player2RightKey.name());
            properties.setProperty("player2BombKey", player2BombKey.name());

            properties.store(output, null);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde des paramètres.");
        }
    }

    public void resetToDefaults() {
        player1UpKey = KeyCode.Z;
        player1DownKey = KeyCode.S;
        player1LeftKey = KeyCode.Q;
        player1RightKey = KeyCode.D;
        player1BombKey = KeyCode.SPACE;

        player2UpKey = KeyCode.UP;
        player2DownKey = KeyCode.DOWN;
        player2LeftKey = KeyCode.LEFT;
        player2RightKey = KeyCode.RIGHT;
        player2BombKey = KeyCode.P;
    }

    // Getters and setters

    public KeyCode getPlayer1UpKey() { return player1UpKey; }

    public void setPlayer1UpKey(KeyCode key) { player1UpKey = key; }

    public KeyCode getPlayer1DownKey() { return player1DownKey; }

    public void setPlayer1DownKey(KeyCode key) { player1DownKey = key; }

    public KeyCode getPlayer1LeftKey() { return player1LeftKey; }

    public void setPlayer1LeftKey(KeyCode key) { player1LeftKey = key; }

    public KeyCode getPlayer1RightKey() { return player1RightKey; }

    public void setPlayer1RightKey(KeyCode key) { player1RightKey = key; }

    public KeyCode getPlayer1BombKey() { return player1BombKey; }

    public void setPlayer1BombKey(KeyCode key) { player1BombKey = key; }

    public KeyCode getPlayer2UpKey() { return player2UpKey; }

    public void setPlayer2UpKey(KeyCode key) { player2UpKey = key; }

    public KeyCode getPlayer2DownKey() { return player2DownKey; }

    public void setPlayer2DownKey(KeyCode key) { player2DownKey = key; }

    public KeyCode getPlayer2LeftKey() { return player2LeftKey; }

    public void setPlayer2LeftKey(KeyCode key) { player2LeftKey = key; }

    public KeyCode getPlayer2RightKey() { return player2RightKey; }

    public void setPlayer2RightKey(KeyCode key) { player2RightKey = key; }

    public KeyCode getPlayer2BombKey() { return player2BombKey; }

    public void setPlayer2BombKey(KeyCode key) { player2BombKey = key; }
}