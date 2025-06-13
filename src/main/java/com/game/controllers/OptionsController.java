package com.game.controllers;

import com.game.utils.InputHandler;
import com.game.utils.InputHandler.PlayerControls;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller responsible for handling the options/settings menu in the game.
 * <p>
 * This includes managing player control key bindings, resetting to defaults, saving changes,
 * and canceling modifications. It also includes sound effect setup for user interaction.
 */
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
     * Initializes the options controller, sets up key capture,
     * loads current control settings, and applies hover sound effects.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
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

    /**
     * Loads the current settings from the input handler and updates the UI.
     */
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
     * Updates the button's text to represent the given key code.
     *
     * @param button  The button to update.
     * @param keyCode The key code to display.
     */
    void updateKeyButtonText(Button button, KeyCode keyCode) {
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

    /**
     * Configures the key capture pane to listen for key presses.
     */
    private void setupKeyCapture() {
        keyCapture.setOnKeyPressed(this::handleKeyCapture);
        keyCapture.setFocusTraversable(true);
    }

    /**
     * Handles the key press event when waiting for a new key binding.
     *
     * @param event The key event captured.
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
     * Clears any existing key assignment if it's already used by another action.
     *
     * @param keyCode The key to clear from any other binding.
     */
    void clearKeyAssignment(KeyCode keyCode) {
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
     * Updates the key binding for a specific player and action.
     *
     * @param player  The player identifier ("player1" or "player2").
     * @param action  The action to bind (e.g., "up", "down").
     * @param keyCode The new key code to assign.
     */
    void updateKeyBinding(String player, String action, KeyCode keyCode) {
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
     * Displays the key capture overlay to wait for user input.
     *
     * @param button The button being modified.
     * @param player The player ("player1" or "player2").
     * @param action The action to modify.
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
     * Returns a localized string for a given action name.
     *
     * @param action The action identifier.
     * @return The localized name of the action.
     */
    private String getActionDisplayName(String action) {
        return switch (action) {
            case "up" -> "Haut";
            case "down" -> "Bas";
            case "left" -> "Gauche";
            case "right" -> "Droite";
            case "bomb" -> "Bombe";
            default -> action;
        };
    }

    /**
     * Hides the key capture overlay and clears the current context.
     */
    private void hideKeyCapture() {
        keyCapture.setVisible(false);
        currentKeyButton = null;
        currentPlayer = null;
        currentAction = null;
    }

    /**
     * Event handler to start changing Player 1's "up" key.
     */
    @FXML
    private void changePlayer1Up() { showKeyCapture(player1UpButton, "player1", "up"); }
    /**
     * Event handler to start changing Player 1's "down" key.
     */
    @FXML
    private void changePlayer1Down() { showKeyCapture(player1DownButton, "player1", "down"); }
    /**
     * Event handler to start changing Player 1's "right" key.
     */
    @FXML
    private void changePlayer1Left() { showKeyCapture(player1LeftButton, "player1", "left"); }
    /**
     * Event handler to start changing Player 1's "left" key.
     */
    @FXML
    private void changePlayer1Right() { showKeyCapture(player1RightButton, "player1", "right"); }
    /**
     * Event handler to start changing Player 1's "bomb" key.
     */
    @FXML
    private void changePlayer1Bomb() { showKeyCapture(player1BombButton, "player1", "bomb"); }

    /**
     * Event handler to start changing Player 2's "up" key.
     */
    @FXML
    private void changePlayer2Up() { showKeyCapture(player2UpButton, "player2", "up"); }
    /**
     * Event handler to start changing Player 2's "down" key.
     */
    @FXML
    private void changePlayer2Down() { showKeyCapture(player2DownButton, "player2", "down"); }
    /**
     * Event handler to start changing Player 2's "left" key.
     */
    @FXML
    private void changePlayer2Left() { showKeyCapture(player2LeftButton, "player2", "left"); }
    /**
     * Event handler to start changing Player 2's "right" key.
     */
    @FXML
    private void changePlayer2Right() { showKeyCapture(player2RightButton, "player2", "right"); }
    /**
     * Event handler to start changing Player 2's "bomb" key.
     */
    @FXML
    private void changePlayer2Bomb() { showKeyCapture(player2BombButton, "player2", "bomb"); }

    /**
     * Cancels the key capture operation and hides the capture overlay.
     */
    @FXML
    private void cancelKeyCapture() {
        hideKeyCapture();
    }

    /**
     * Restores all player control settings to default values.
     * Displays a confirmation dialog before resetting.
     */
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

    /**
     * Saves the current control settings and displays a confirmation alert.
     */
    @FXML
    void applySettings() {
        inputHandler.saveSettings();  // Il faut implémenter cette méthode dans InputHandler

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres sauvegardés");
        alert.setHeaderText("Succès");
        alert.setContentText("Les paramètres ont été sauvegardés avec succès !");
        alert.showAndWait();
    }

    /**
     * Cancels any unsaved changes and reloads the last saved settings.
     */
    @FXML
    void cancelSettings() {
        inputHandler.loadConfiguration(); // Reload config
        inputHandler.convertStringKeysToKeyCodes();
        loadCurrentSettings();
    }

    /**
     * Navigates back to the main menu scene.
     */
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

    // === Player 1 Buttons ===

    /**
     * Gets the button for Player 1's "Move Up" action.
     * @return the Player 1 up button
     */
    public Button getPlayer1UpButton() {
        return player1UpButton;
    }

    /**
     * Sets the button for Player 1's "Move Up" action.
     * @param player1UpButton the button to assign
     */
    public void setPlayer1UpButton(Button player1UpButton) {
        this.player1UpButton = player1UpButton;
    }

    /**
     * Gets the button for Player 1's "Move Down" action.
     * @return the Player 1 down button
     */
    public Button getPlayer1DownButton() {
        return player1DownButton;
    }

    /**
     * Sets the button for Player 1's "Move Down" action.
     * @param player1DownButton the button to assign
     */
    public void setPlayer1DownButton(Button player1DownButton) {
        this.player1DownButton = player1DownButton;
    }

    /**
     * Gets the button for Player 1's "Move Left" action.
     * @return the Player 1 left button
     */
    public Button getPlayer1LeftButton() {
        return player1LeftButton;
    }

    /**
     * Sets the button for Player 1's "Move Left" action.
     * @param player1LeftButton the button to assign
     */
    public void setPlayer1LeftButton(Button player1LeftButton) {
        this.player1LeftButton = player1LeftButton;
    }

    /**
     * Gets the button for Player 1's "Move Right" action.
     * @return the Player 1 right button
     */
    public Button getPlayer1RightButton() {
        return player1RightButton;
    }

    /**
     * Sets the button for Player 1's "Move Right" action.
     * @param player1RightButton the button to assign
     */
    public void setPlayer1RightButton(Button player1RightButton) {
        this.player1RightButton = player1RightButton;
    }

    /**
     * Gets the button for Player 1's "Place Bomb" action.
     * @return the Player 1 bomb button
     */
    public Button getPlayer1BombButton() {
        return player1BombButton;
    }

    /**
     * Sets the button for Player 1's "Place Bomb" action.
     * @param player1BombButton the button to assign
     */
    public void setPlayer1BombButton(Button player1BombButton) {
        this.player1BombButton = player1BombButton;
    }


// === Player 2 Buttons ===

    /**
     * Gets the button for Player 2's "Move Up" action.
     * @return the Player 2 up button
     */
    public Button getPlayer2UpButton() {
        return player2UpButton;
    }

    /**
     * Sets the button for Player 2's "Move Up" action.
     * @param player2UpButton the button to assign
     */
    public void setPlayer2UpButton(Button player2UpButton) {
        this.player2UpButton = player2UpButton;
    }

    /**
     * Gets the button for Player 2's "Move Down" action.
     * @return the Player 2 down button
     */
    public Button getPlayer2DownButton() {
        return player2DownButton;
    }

    /**
     * Sets the button for Player 2's "Move Down" action.
     * @param player2DownButton the button to assign
     */
    public void setPlayer2DownButton(Button player2DownButton) {
        this.player2DownButton = player2DownButton;
    }

    /**
     * Gets the button for Player 2's "Move Left" action.
     * @return the Player 2 left button
     */
    public Button getPlayer2LeftButton() {
        return player2LeftButton;
    }

    /**
     * Sets the button for Player 2's "Move Left" action.
     * @param player2LeftButton the button to assign
     */
    public void setPlayer2LeftButton(Button player2LeftButton) {
        this.player2LeftButton = player2LeftButton;
    }

    /**
     * Gets the button for Player 2's "Move Right" action.
     * @return the Player 2 right button
     */
    public Button getPlayer2RightButton() {
        return player2RightButton;
    }

    /**
     * Sets the button for Player 2's "Move Right" action.
     * @param player2RightButton the button to assign
     */
    public void setPlayer2RightButton(Button player2RightButton) {
        this.player2RightButton = player2RightButton;
    }

    /**
     * Gets the button for Player 2's "Place Bomb" action.
     * @return the Player 2 bomb button
     */
    public Button getPlayer2BombButton() {
        return player2BombButton;
    }

    /**
     * Sets the button for Player 2's "Place Bomb" action.
     * @param player2BombButton the button to assign
     */
    public void setPlayer2BombButton(Button player2BombButton) {
        this.player2BombButton = player2BombButton;
    }


// === Other Buttons ===

    /**
     * Gets the reset button.
     * @return the reset button
     */
    public Button getResetButton() {
        return resetButton;
    }

    /**
     * Sets the reset button.
     * @param resetButton the button to assign
     */
    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
    }

    /**
     * Gets the apply button.
     * @return the apply button
     */
    public Button getApplyButton() {
        return applyButton;
    }

    /**
     * Sets the apply button.
     * @param applyButton the button to assign
     */
    public void setApplyButton(Button applyButton) {
        this.applyButton = applyButton;
    }

    /**
     * Gets the cancel button.
     * @return the cancel button
     */
    public Button getCancelButton() {
        return cancelButton;
    }

    /**
     * Sets the cancel button.
     * @param cancelButton the button to assign
     */
    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    /**
     * Gets the back button.
     * @return the back button
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Sets the back button.
     * @param backButton the button to assign
     */
    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }


// === Key Capture UI Elements ===

    /**
     * Gets the StackPane used for capturing key input.
     * @return the key capture pane
     */
    public StackPane getKeyCapture() {
        return keyCapture;
    }

    /**
     * Sets the StackPane used for capturing key input.
     * @param keyCapture the StackPane to assign
     */
    public void setKeyCapture(StackPane keyCapture) {
        this.keyCapture = keyCapture;
    }

    /**
     * Gets the label displaying key capture instructions or state.
     * @return the key capture label
     */
    public Label getKeyCaptureLabel() {
        return keyCaptureLabel;
    }

    /**
     * Sets the label displaying key capture instructions or state.
     * @param keyCaptureLabel the label to assign
     */
    public void setKeyCaptureLabel(Label keyCaptureLabel) {
        this.keyCaptureLabel = keyCaptureLabel;
    }


// === Other Variables ===

    /**
     * Gets the current input handler.
     * @return the input handler
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * Sets the input handler.
     * @param inputHandler the input handler to assign
     */
    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    /**
     * Gets the currently selected button for key remapping.
     * @return the current key button
     */
    public Button getCurrentKeyButton() {
        return currentKeyButton;
    }

    /**
     * Sets the currently selected button for key remapping.
     * @param currentKeyButton the button to assign
     */
    public void setCurrentKeyButton(Button currentKeyButton) {
        this.currentKeyButton = currentKeyButton;
    }

    /**
     * Gets the identifier for the current player (e.g., "Player1", "Player2").
     * @return the current player identifier
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the identifier for the current player.
     * @param currentPlayer the player identifier to assign
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Gets the name of the current action being configured (e.g., "UP", "BOMB").
     * @return the current action name
     */
    public String getCurrentAction() {
        return currentAction;
    }

    /**
     * Sets the name of the current action being configured.
     * @param currentAction the action name to assign
     */
    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

}
