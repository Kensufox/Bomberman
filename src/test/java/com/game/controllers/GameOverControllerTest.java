package com.game.controllers;

import com.game.JavaFXInitializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class GameOverControllerTest {

    private GameOverController controller;
    Label winnerLabel;
    Label scoreJ1 ;
    Label scoreJ2;


    @BeforeAll
    public static void setUpOnce() {
        JavaFXInitializer.init();
    }

    @BeforeEach
    void setUp() {
        controller = new GameOverController();
        // Inject mock labels
        winnerLabel = new Label();
        scoreJ1 = new Label();
        scoreJ2 = new Label();

        // Use reflection to set private fields
        try {
            java.lang.reflect.Field winnerField = GameOverController.class.getDeclaredField("winnerLabel");
            winnerField.setAccessible(true);
            winnerField.set(controller, winnerLabel);

            java.lang.reflect.Field scoreJ1Field = GameOverController.class.getDeclaredField("scoreJ1");
            scoreJ1Field.setAccessible(true);
            scoreJ1Field.set(controller, scoreJ1);

            java.lang.reflect.Field scoreJ2Field = GameOverController.class.getDeclaredField("scoreJ2");
            scoreJ2Field.setAccessible(true);
            scoreJ2Field.set(controller, scoreJ2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSetWinnerText() {
        controller.setWinnerText("Player 1 Wins!");
        assertEquals("Player 1 Wins!", controller.getWinnerLabel().getText());
    }

    @Test
    void testSetPlayersScore() {
        controller.setPlayersScore(10, 20);
        assertEquals("Player 1 Score : 10", controller.getScoreJ1().getText());
        assertEquals("Player 2 Score : 20", controller.getScoreJ2().getText());
    }

    @Test
    void testRetourMenuHandlesIOExceptionGracefully() {
        // Mock ActionEvent and Button
        Button mockButton = mock(Button.class);
        javafx.scene.Scene mockScene = mock(javafx.scene.Scene.class);
        when(mockButton.getScene()).thenReturn(mockScene);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getSource()).thenReturn(mockButton);

        // Spy on controller to mock FXMLLoader behavior
        GameOverController spyController = Mockito.spy(controller);

        // Simulate IOException by mocking FXMLLoader
        try {
            FXMLLoader mockLoader = mock(FXMLLoader.class);
            when(mockLoader.load()).thenThrow(new IOException());
            doReturn(mockLoader).when(spyController).createLoader(anyString());
        } catch (Exception e) {
            fail("Mock setup failed");
        }

        // Since the catch block is empty, just ensure no exception is thrown
        assertDoesNotThrow(() -> controller.retourMenu(mockEvent));
    }


}