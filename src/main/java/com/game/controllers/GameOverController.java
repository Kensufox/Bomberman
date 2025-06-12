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
    /**
     * Gets the label displaying the winner.
     *
     * @return the winner label
     */
    public Label getWinnerLabel() {
        return winnerLabel;
    }

    /**
     * Sets the label displaying the winner.
     *
     * @param winnerLabel the winner label to set
     */
    public void setWinnerLabel(Label winnerLabel) {
        this.winnerLabel = winnerLabel;
    }

    /**
     * Gets the label displaying the score of player 1.
     *
     * @return the label for player 1's score
     */
    public Label getScoreJ1() {
        return scoreJ1;
    }

    /**
     * Sets the label displaying the score of player 1.
     *
     * @param scoreJ1 the label to set for player 1's score
     */
    public void setScoreJ1(Label scoreJ1) {
        this.scoreJ1 = scoreJ1;
    }

    /**
     * Gets the label displaying the score of player 2.
     *
     * @return the label for player 2's score
     */
    public Label getScoreJ2() {
        return scoreJ2;
    }

    /**
     * Sets the label displaying the score of player 2.
     *
     * @param scoreJ2 the label to set for player 2's score
     */
    public void setScoreJ2(Label scoreJ2) {
        this.scoreJ2 = scoreJ2;
    }

    /**
     * Creates and returns a new {@link FXMLLoader} for the specified FXML resource path.
     *
     * @param resource the path to the FXML resource, relative to the classpath
     * @return a new {@link FXMLLoader} instance for the given resource
     */
    protected FXMLLoader createLoader(String resource) {
        return new FXMLLoader(getClass().getResource(resource));
    }


}
