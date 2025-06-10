package com.game.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.game.utils.InputHandler;
import com.game.utils.InputHandler.PlayerControls;
import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class OptionsController implements Initializable {

    @FXML private Button player1UpButton;
    @FXML private Button player1DownButton;
    @FXML private Button player1LeftButton;
    @FXML private Button player1RightButton;
    @FXML private Button player1BombButton;

    @FXML private Button player2UpButton;
    @FXML private Button player2DownButton;
    @FXML private Button player2LeftButton;
    @FXML private Button player2RightButton;
    @FXML private Button player2BombButton;

    @FXML private Button resetButton;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    @FXML private StackPane keyCapture;
    @FXML private Label keyCaptureLabel;

    private InputHandler inputHandler;

    private Button currentKeyButton;
    private String currentPlayer;
    private String currentAction;

    /** 
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputHandler = new InputHandler();
        loadCurrentSettings();
        setupKeyCapture();

        SFXPlayer.setupHoverSound(player1BombButton);
        SFXPlayer.setupHoverSound(player1UpButton);
        SFXPlayer.setupHoverSound(player1DownButton);
        SFXPlayer.setupHoverSound(player1LeftButton);
        SFXPlayer.setupHoverSound(player1RightButton);
        
        SFXPlayer.setupHoverSound(player2BombButton);
        SFXPlayer.setupHoverSound(player2UpButton);
        SFXPlayer.setupHoverSound(player2DownButton);
        SFXPlayer.setupHoverSound(player2LeftButton);
        SFXPlayer.setupHoverSound(player2RightButton);
        
        SFXPlayer.setupHoverSound(resetButton);
        SFXPlayer.setupHoverSound(applyButton);
        SFXPlayer.setupHoverSound(cancelButton);
        SFXPlayer.setupHoverSound(backButton);
    }

    private void loadCurrentSettings() {
        PlayerControls j1 = inputHandler.getJ1Controls();
        PlayerControls j2 = inputHandler.getJ2Controls();

        updateKeyButtonText(player1UpButton, j1.up);
        updateKeyButtonText(player1DownButton, j1.down);
        updateKeyButtonText(player1LeftButton, j1.left);
        updateKeyButtonText(player1RightButton, j1.right);
        updateKeyButtonText(player1BombButton, j1.bomb);

        updateKeyButtonText(player2UpButton, j2.up);
        updateKeyButtonText(player2DownButton, j2.down);
        updateKeyButtonText(player2LeftButton, j2.left);
        updateKeyButtonText(player2RightButton, j2.right);
        updateKeyButtonText(player2BombButton, j2.bomb);
    }

    /** 
     * @param button
     * @param keyCode
     */
    private void updateKeyButtonText(Button button, KeyCode keyCode) {
        if (keyCode == null) {
            button.setText("NONE");
            return;
        }
        switch (keyCode) {
            case UP: button.setText("↑"); break;
            case DOWN: button.setText("↓"); break;
            case LEFT: button.setText("←"); break;
            case RIGHT: button.setText("→"); break;
            case SPACE: button.setText("SPACE"); break;
            default: button.setText(keyCode.getName().toUpperCase()); break;
        }
    }

    private void setupKeyCapture() {
        keyCapture.setOnKeyPressed(this::handleKeyCapture);
        keyCapture.setFocusTraversable(true);
    }

    /** 
     * @param event
     */
    private void handleKeyCapture(KeyEvent event) {
        if (currentKeyButton != null) {
            KeyCode newKey = event.getCode();

            // Enlever si déjà utilisée
            clearKeyAssignment(newKey);

            updateKeyButtonText(currentKeyButton, newKey);
            updateKeyBinding(currentPlayer, currentAction, newKey);

            hideKeyCapture();
        }
        event.consume();
    }

    /** 
     * @param keyCode
     */
    private void clearKeyAssignment(KeyCode keyCode) {
        if (inputHandler.getJ1Up() == keyCode) inputHandler.setJ1Up(null);
        else if (inputHandler.getJ1Down() == keyCode) inputHandler.setJ1Down(null);
        else if (inputHandler.getJ1Left() == keyCode) inputHandler.setJ1Left(null);
        else if (inputHandler.getJ1Right() == keyCode) inputHandler.setJ1Right(null);
        else if (inputHandler.getJ1Bomb() == keyCode) inputHandler.setJ1Bomb(null);
        else if (inputHandler.getJ2Up() == keyCode) inputHandler.setJ2Up(null);
        else if (inputHandler.getJ2Down() == keyCode) inputHandler.setJ2Down(null);
        else if (inputHandler.getJ2Left() == keyCode) inputHandler.setJ2Left(null);
        else if (inputHandler.getJ2Right() == keyCode) inputHandler.setJ2Right(null);
        else if (inputHandler.getJ2Bomb() == keyCode) inputHandler.setJ2Bomb(null);
    }

    /** 
     * @param player
     * @param action
     * @param keyCode
     */
    private void updateKeyBinding(String player, String action, KeyCode keyCode) {
        if ("player1".equals(player)) {
            switch (action) {
                case "up": inputHandler.setJ1Up(keyCode); break;
                case "down": inputHandler.setJ1Down(keyCode); break;
                case "left": inputHandler.setJ1Left(keyCode); break;
                case "right": inputHandler.setJ1Right(keyCode); break;
                case "bomb": inputHandler.setJ1Bomb(keyCode); break;
            }
        } else if ("player2".equals(player)) {
            switch (action) {
                case "up": inputHandler.setJ2Up(keyCode); break;
                case "down": inputHandler.setJ2Down(keyCode); break;
                case "left": inputHandler.setJ2Left(keyCode); break;
                case "right": inputHandler.setJ2Right(keyCode); break;
                case "bomb": inputHandler.setJ2Bomb(keyCode); break;
            }
        }
    }

    /** 
     * @param button
     * @param player
     * @param action
     */
    private void showKeyCapture(Button button, String player, String action) {
        currentKeyButton = button;
        currentPlayer = player;
        currentAction = action;

        keyCaptureLabel.setText("Appuyez sur une touche pour " +
                getActionDisplayName(action) + " - Joueur " +
                (player.equals("player1") ? "1" : "2"));

        keyCapture.setVisible(true);
        keyCapture.requestFocus();
    }

    /** 
     * @param action
     * @return String
     */
    private String getActionDisplayName(String action) {
        switch (action) {
            case "up": return "Haut";
            case "down": return "Bas";
            case "left": return "Gauche";
            case "right": return "Droite";
            case "bomb": return "Bombe";
            default: return action;
        }
    }

    private void hideKeyCapture() {
        keyCapture.setVisible(false);
        currentKeyButton = null;
        currentPlayer = null;
        currentAction = null;
    }

    /** 
     * @param cancelKeyCapture(
     */
    // Event handlers for buttons:

    @FXML
    private void changePlayer1Up() { showKeyCapture(player1UpButton, "player1", "up"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer1Down() { showKeyCapture(player1DownButton, "player1", "down"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer1Left() { showKeyCapture(player1LeftButton, "player1", "left"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer1Right() { showKeyCapture(player1RightButton, "player1", "right"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer1Bomb() { showKeyCapture(player1BombButton, "player1", "bomb"); }

    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer2Up() { showKeyCapture(player2UpButton, "player2", "up"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer2Down() { showKeyCapture(player2DownButton, "player2", "down"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer2Left() { showKeyCapture(player2LeftButton, "player2", "left"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer2Right() { showKeyCapture(player2RightButton, "player2", "right"); }
    /** 
     * @param cancelKeyCapture(
     */
    @FXML
    private void changePlayer2Bomb() { showKeyCapture(player2BombButton, "player2", "bomb"); }

    @FXML
    private void cancelKeyCapture() {
        hideKeyCapture();
    }

    @FXML
    private void resetToDefaults() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réinitialiser");
        alert.setHeaderText("Réinitialiser les paramètres");
        alert.setContentText("Êtes-vous sûr de vouloir réinitialiser tous les paramètres aux valeurs par défaut ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            inputHandler.resetToDefaults();  // Il faut implémenter cette méthode dans InputHandler
            loadCurrentSettings();
        }
    }

    @FXML
    private void applySettings() {
        inputHandler.saveSettings();  // Il faut implémenter cette méthode dans InputHandler

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres sauvegardés");
        alert.setHeaderText("Succès");
        alert.setContentText("Les paramètres ont été sauvegardés avec succès !");
        alert.showAndWait();
    }

    @FXML
    private void cancelSettings() {
        inputHandler.loadConfiguration(); // Reload config
        inputHandler.convertStringKeysToKeyCodes();
        loadCurrentSettings();
    }

    @FXML
    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
