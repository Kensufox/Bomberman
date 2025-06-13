package com.game.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.input.KeyCode;

class InputHandlerTest {

    private static final String CONFIG_PATH = "config.properties";
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(CONFIG_PATH)); // Ensure no config file exists
        inputHandler = new InputHandler();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(CONFIG_PATH)); // Clean up after tests
    }

    @Test
    void testDefaultKeyBindings() {
        InputHandler.PlayerControls j1 = inputHandler.getJ1Controls();
        InputHandler.PlayerControls j2 = inputHandler.getJ2Controls();

        assertEquals(KeyCode.Z, j1.up);
        assertEquals(KeyCode.S, j1.down);
        assertEquals(KeyCode.Q, j1.left);
        assertEquals(KeyCode.D, j1.right);
        assertEquals(KeyCode.SPACE, j1.bomb);

        assertEquals(KeyCode.UP, j2.up);
        assertEquals(KeyCode.DOWN, j2.down);
        assertEquals(KeyCode.LEFT, j2.left);
        assertEquals(KeyCode.RIGHT, j2.right);
        assertEquals(KeyCode.CONTROL, j2.bomb);
    }

    @Test
    void testSetAndGetPlayer1Controls() {
        inputHandler.setJ1Up(KeyCode.A);
        inputHandler.setJ1Down(KeyCode.B);
        inputHandler.setJ1Left(KeyCode.C);
        inputHandler.setJ1Right(KeyCode.DIGIT1);
        inputHandler.setJ1Bomb(KeyCode.ENTER);

        assertEquals(KeyCode.A, inputHandler.getJ1Up());
        assertEquals(KeyCode.B, inputHandler.getJ1Down());
        assertEquals(KeyCode.C, inputHandler.getJ1Left());
        assertEquals(KeyCode.DIGIT1, inputHandler.getJ1Right());
        assertEquals(KeyCode.ENTER, inputHandler.getJ1Bomb());
    }

    @Test
    void testSetAndGetPlayer2Controls() {
        inputHandler.setJ2Up(KeyCode.T);
        inputHandler.setJ2Down(KeyCode.G);
        inputHandler.setJ2Left(KeyCode.F);
        inputHandler.setJ2Right(KeyCode.H);
        inputHandler.setJ2Bomb(KeyCode.SHIFT);

        assertEquals(KeyCode.T, inputHandler.getJ2Up());
        assertEquals(KeyCode.G, inputHandler.getJ2Down());
        assertEquals(KeyCode.F, inputHandler.getJ2Left());
        assertEquals(KeyCode.H, inputHandler.getJ2Right());
        assertEquals(KeyCode.SHIFT, inputHandler.getJ2Bomb());
    }

    @Test
    void testSaveAndLoadSettings() {
        // Set custom keys and save
        inputHandler.setJ1Up(KeyCode.A);
        inputHandler.setJ2Down(KeyCode.K);
        inputHandler.saveSettings();

        // Reload from file
        InputHandler newHandler = new InputHandler();

        assertEquals(KeyCode.A, newHandler.getJ1Up());
        assertEquals(KeyCode.K, newHandler.getJ2Down());
    }

    @Test
    void testStringToKeyCodeConversion() {
        assertEquals(KeyCode.SPACE, inputHandler.stringToKeyCode("space"));
        assertEquals(KeyCode.UP, inputHandler.stringToKeyCode("UP"));
        assertEquals(KeyCode.CONTROL, inputHandler.stringToKeyCode("control"));
        assertEquals(KeyCode.A, inputHandler.stringToKeyCode("A"));
        assertNull(inputHandler.stringToKeyCode("invalid_key"));
    }

    @Test
    void testResetToDefaults() {
        inputHandler.setJ1Up(KeyCode.X);
        inputHandler.setJ2Bomb(KeyCode.Z);
        inputHandler.resetToDefaults();

        assertEquals(KeyCode.Z, inputHandler.getJ1Up());
        assertEquals(KeyCode.CONTROL, inputHandler.getJ2Bomb());
    }
}
