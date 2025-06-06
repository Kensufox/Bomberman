package com.game.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameOverController {

    @FXML
    private Label winnerLabel;

    public void setWinnerText(String text) {
        winnerLabel.setText(text);
    }
}
