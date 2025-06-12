package com.game.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Controller for the Game Over screen in the game.
 * <p>
 * This class handles the display of the winner and final scores for each player,
 * and provides functionality to return to the main menu.
 */
public class GameOverController {

    /**
     * Default constructor for GameOverController.
     */
    public GameOverController() {
        // No initialization needed currently
    }

    @FXML
    private Label winnerLabel;

    @FXML
    private Label scoreJ1;

    @FXML
    private Label scoreJ2;

    /**
     * Sets the winner text displayed on the Game Over screen.
     *
     * @param text The text to be displayed as the winner announcement.
     */
    public void setWinnerText(String text) {
        winnerLabel.setText(text);
    }

    /**
     * Sets the final scores for both players on the Game Over screen.
     *
     * @param P1Score The final score of Player 1.
     * @param P2Score The final score of Player 2.
     */
    public void setPlayersScore(int P1Score, int P2Score) {
        scoreJ1.setText("Player 1 Score : " + P1Score);
        scoreJ2.setText("Player 2 Score : " + P2Score);
    }

    /**
     * Handles the event triggered when the user clicks the "Return to Menu" button.
     * Loads the main menu scene and replaces the current scene content with it.
     *
     * @param event The action event triggered by the user's interaction.
     */
    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
        }
    }
}
