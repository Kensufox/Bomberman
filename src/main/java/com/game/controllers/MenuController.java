package com.game.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MenuController {

    // Menu Principal
    @FXML private Button jouer;
    @FXML private Button options;
    @FXML private Button quitter;

    // Menu Jouer
    @FXML private Button btnClassic;
    @FXML private Button btnCaptureTheFlag;
    @FXML private Button btnContreLOrdi;

    // Menu Niveau Bot
    @FXML private Button btnEasyBot;
    @FXML private Button btnMediumBot;
    @FXML private Button btnHardBot;
    @FXML private Button btnRetourChooseGame;

    @FXML
    public void jouer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choose_game.fxml"));
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void optionsMenu (ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/option_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exit () {
        System.exit(0);
    }

    // MENU JOUER
    @FXML
    public void classicGame (ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_map.fxml"));
            loader.setController(new GameMapController());
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void VsComputer (ActionEvent event) {
        try {
            // Au lieu d'aller directement au jeu, on va à la sélection du niveau
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bot_level_selection.fxml"));
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MENU NIVEAU BOT
    @FXML
    public void easyBot(ActionEvent event) {
        startBotGame(event, "EASY");
    }

    @FXML
    public void mediumBot(ActionEvent event) {
        startBotGame(event, "MEDIUM");
    }

    @FXML
    public void hardBot(ActionEvent event) {
        startBotGame(event, "HARD");
    }

    private void startBotGame(ActionEvent event, String difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_map.fxml"));
            GameMapControllerbot controller = new GameMapControllerbot();
            controller.setBotDifficulty(difficulty); // Définir la difficulté
            loader.setController(controller);
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourChooseGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choose_game.fxml"));
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
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