package com.game.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GameOverController {

    @FXML
    private Label winnerLabel;

    @FXML
    private Label scoreJ1;

    @FXML
    private Label scoreJ2;

    public void setWinnerText(String text) {
        winnerLabel.setText(text);
    }

    public void setPlayersScore(int P1Score, int P2Score) {
        scoreJ1.setText("Player 1 Score : " + P1Score);
        scoreJ2.setText("Player 2 Score : " + P2Score);
    }

    @FXML
    public void retourMenu (ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
