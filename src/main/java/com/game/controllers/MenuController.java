package com.game.controllers;

import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class MenuController {

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
     * @param event
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * @param event
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exit () {
        System.exit(0);
    }

    /** 
     * @param event
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * @param event
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 
     * @param event
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}