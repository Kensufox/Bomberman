package com.game.controllers;

import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controller for managing the main menu and submenus of the game.
 * <p>
 * This class handles navigation between the main menu, game mode selection,
 * and options screen. It also initializes sound effects for button interactions.
 */

public class MenuController {

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up hover sound effects for each button.
     */
    @FXML
    public void initialize() {
        SFXPlayer.setupHoverSound(jouer);
        SFXPlayer.setupHoverSound(options);
        SFXPlayer.setupHoverSound(quitter);
        SFXPlayer.setupHoverSound(btnClassic);
        SFXPlayer.setupHoverSound(btnCaptureTheFlag);
        SFXPlayer.setupHoverSound(btnContreLOrdi);
        SFXPlayer.setupHoverSound(profils);
    }

    // Menu Principal

    @FXML private Button jouer;
    @FXML private Button profils;
    @FXML private Button options;
    @FXML private Button quitter;

    // Menu Jouer
    @FXML private Button btnClassic;
    @FXML private Button btnCaptureTheFlag;
    @FXML private Button btnContreLOrdi;
    @FXML private Button btnLevelEditor;

    // Menu Niveau Bot
    @FXML private Button btnEasyBot;
    @FXML private Button btnMediumBot;
    @FXML private Button btnHardBot;
    @FXML private Button btnRetourChooseGame;

    /**
     * Handles the action event triggered when the "Play" button is clicked.
     * Navigates to the game mode selection screen.
     *
     * @param event The action event triggered by the "Play" button.
     */
    @FXML
    public void jouer(ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choose_game.fxml"));
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action event triggered when the "Profiles" button is clicked.
     * Navigates to the player profiles management screen.
     *
     * @param event The action event triggered by the "Profiles" button.
     */
    @FXML
    public void gestionProfils(ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profils_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
        }
    }

    /**
     * Handles the action event triggered when the "Options" button is clicked.
     * Navigates to the options menu screen.
     *
     * @param event The action event triggered by the "Options" button.
     */
    @FXML
    public void optionsMenu (ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/option_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the application when the "Quit" button is clicked.
     */
    @FXML
    public void exit () {
        System.exit(0);
    }

    /**
     * Handles the action event triggered when the "Classic Game" button is clicked.
     * Launches the classic game mode.
     *
     * @param event The action event triggered by the "Classic Game" button.
     */
    // MENU JOUER
    @FXML
    public void classicGame (ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_map.fxml"));
            loader.setController(new GameMapController());
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the action event triggered when the "Capture The Flag" button is clicked.
     * Launches the capture the flag game mode.
     *
     * @param event The action event triggered by the "Capture The Flag" button.
     */
    @FXML
    public void CptFlag (ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_map.fxml"));
            loader.setController(new GameMapControllerFlag());
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action event triggered when the "Versus Computer" button is clicked.
     * Launches the game mode where the player plays against the computer.
     *
     * @param event The action event triggered by the "Versus Computer" button.
     */
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
    /**
     * Starts an easy bot game mode.
     *
     * @param event the action event triggered by the "Easy Bot" button
     */
    @FXML
    public void easyBot(ActionEvent event) {
        startBotGame(event, "EASY");
    }

    /**
     * Starts a medium bot game mode.
     *
     * @param event the action event triggered by the "Medium Bot" button
     */
    @FXML
    public void mediumBot(ActionEvent event) {
        startBotGame(event, "MEDIUM");
    }

    /**
     * Starts a hard bot game mode.
     *
     * @param event the action event triggered by the "Hard Bot" button
     */
    @FXML
    public void hardBot(ActionEvent event) {
        startBotGame(event, "HARD");
    }

    /**
     * Helper method to launch a bot game with the specified difficulty.
     *
     * @param event      the action event triggering the game start
     * @param difficulty the difficulty level of the bot ("EASY", "MEDIUM", or "HARD")
     */
    private void startBotGame(ActionEvent event, String difficulty) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
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

    /**
     * Handles the "Back to Choose Game" button click event.
     * Returns to the game mode selection screen.
     *
     * @param event the action event triggered by the "Back to Choose Game" button
     */
    @FXML
    public void retourChooseGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/choose_game.fxml"));
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action event triggered when the "Level Editor" button is clicked.
     * Launches the level editor to create custom map
     *
     * @param event The action event triggered by the "Level Editor" button.
     */
    @FXML
    public void LevelEditor (ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/level-editor.fxml"));
            //loader.setController(new LevelEditorController());
            BorderPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action event triggered when the "Back to Menu" button is clicked.
     * Returns to the main menu.
     *
     * @param event The action event triggered by the "Back to Menu" button.
     */
    @FXML
    public void retourMenu (ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.CANCEL);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            AnchorPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}