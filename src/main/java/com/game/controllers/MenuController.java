package com.game.controllers;

import java.io.IOException;

import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

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
    }

    // Menu Principal

    @FXML private Button jouer;
    @FXML private Button options;
    @FXML private Button quitter;

    // Menu Jouer
    @FXML private Button btnClassic;
    @FXML private Button btnCaptureTheFlag;
    @FXML private Button btnContreLOrdi;

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
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_map.fxml"));
            loader.setController(new GameMapControllerbot());
            StackPane root = loader.load();

            // Retrieves the current scene from one of the buttons
            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
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
        }
    }

}