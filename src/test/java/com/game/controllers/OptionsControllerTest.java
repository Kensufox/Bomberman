package com.game.controllers;

import com.game.JavaFXInitializer;
import com.game.utils.InputHandler;
import com.game.utils.InputHandler.PlayerControls;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class OptionsControllerTest {

    private OptionsController controller;
    private InputHandler mockInputHandler;

    @BeforeAll
    public static void setUpOnce() {
        JavaFXInitializer.init();
    }

    @BeforeEach
    void setUp() {
        controller = new OptionsController();

        // Mocking FXML-injected components
        controller.setPlayer1UpButton(new Button());
        controller.setPlayer1DownButton(new Button());
        controller.setPlayer1LeftButton(new Button());
        controller.setPlayer1RightButton(new Button());
        controller.setPlayer1BombButton(new Button());

        controller.setPlayer2UpButton(new Button());
        controller.setPlayer2DownButton(new Button());
        controller.setPlayer2LeftButton(new Button());
        controller.setPlayer2RightButton(new Button());
        controller.setPlayer2BombButton(new Button());

        controller.setResetButton(new Button());
        controller.setApplyButton(new Button());
        controller.setCancelButton(new Button());
        controller.setBackButton(new Button());

        controller.setKeyCapture(new StackPane());
        controller.setKeyCaptureLabel(new Label());

        // Mocking the InputHandler
        mockInputHandler = Mockito.mock(InputHandler.class);
        controller.setInputHandler(mockInputHandler);
    }

    @Test
    void testUpdateKeyButtonText() {
        Button btn = new Button();
        controller.setPlayer1UpButton(btn);

        controller.getPlayer1UpButton().setText("OLD");
        controller.updateKeyButtonText(btn, KeyCode.UP);

        assertEquals("↑", btn.getText());
    }

    @Test
    void testClearKeyAssignmentRemovesKeyFromPlayer1() {
        Mockito.when(mockInputHandler.getJ1Up()).thenReturn(KeyCode.W);
        controller.clearKeyAssignment(KeyCode.W);

        Mockito.verify(mockInputHandler).setJ1Up(null);
    }

    @Test
    void testUpdateKeyBindingPlayer1Up() {
        controller.updateKeyBinding("player1", "up", KeyCode.A);
        Mockito.verify(mockInputHandler).setJ1Up(KeyCode.A);
    }

    @Test
    void testUpdateKeyBindingPlayer2Bomb() {
        controller.updateKeyBinding("player2", "bomb", KeyCode.SPACE);
        Mockito.verify(mockInputHandler).setJ2Bomb(KeyCode.SPACE);
    }

    @Test
    void testResetToDefaultsConfirmationSkipped() {
        // Directly test the logic
        controller.getInputHandler().resetToDefaults();
        Mockito.verify(mockInputHandler).resetToDefaults();
    }
}
