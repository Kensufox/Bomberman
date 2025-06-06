package com.game.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.game.controllers.GameSettings;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController implements Initializable {

    // Audio Controls
    @FXML private Slider masterVolumeSlider;
    @FXML private Slider sfxVolumeSlider;
    @FXML private Slider musicVolumeSlider;
    @FXML private Label masterVolumeLabel;
    @FXML private Label sfxVolumeLabel;
    @FXML private Label musicVolumeLabel;

    // Player 1 Controls
    @FXML private Button player1UpButton;
    @FXML private Button player1DownButton;
    @FXML private Button player1LeftButton;
    @FXML private Button player1RightButton;
    @FXML private Button player1BombButton;

    // Player 2 Controls
    @FXML private Button player2UpButton;
    @FXML private Button player2DownButton;
    @FXML private Button player2LeftButton;
    @FXML private Button player2RightButton;
    @FXML private Button player2BombButton;

    // Gameplay Controls
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private Slider gameSpeedSlider;
    @FXML private Label gameSpeedLabel;
    @FXML private CheckBox screenShakeCheckBox;
    @FXML private CheckBox particlesCheckBox;

    // Control Buttons
    @FXML private Button resetButton;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    // Key Capture Dialog
    @FXML private StackPane keyCapture;
    @FXML private Label keyCaptureLabel;

    private GameSettings settings;
    private Button currentKeyButton;
    private String currentPlayer;
    private String currentAction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings = GameSettings.getInstance();
        loadCurrentSettings();
        setupKeyCapture();
    }

    private void loadCurrentSettings() {
        // Load key bindings
        updateKeyButtonText(player1UpButton, settings.getPlayer1UpKey());
        updateKeyButtonText(player1DownButton, settings.getPlayer1DownKey());
        updateKeyButtonText(player1LeftButton, settings.getPlayer1LeftKey());
        updateKeyButtonText(player1RightButton, settings.getPlayer1RightKey());
        updateKeyButtonText(player1BombButton, settings.getPlayer1BombKey());

        updateKeyButtonText(player2UpButton, settings.getPlayer2UpKey());
        updateKeyButtonText(player2DownButton, settings.getPlayer2DownKey());
        updateKeyButtonText(player2LeftButton, settings.getPlayer2LeftKey());
        updateKeyButtonText(player2RightButton, settings.getPlayer2RightKey());
        updateKeyButtonText(player2BombButton, settings.getPlayer2BombKey());
    }

    private void updateKeyButtonText(Button button, KeyCode keyCode) {
        String keyText = getKeyDisplayText(keyCode);
        button.setText(keyText);
    }

    private String getKeyDisplayText(KeyCode keyCode) {
        switch (keyCode) {
            case UP: return "↑";
            case DOWN: return "↓";
            case LEFT: return "←";
            case RIGHT: return "→";
            case SPACE: return "SPACE";
            case ENTER: return "ENTER";
            case SHIFT: return "SHIFT";
            case CONTROL: return "CTRL";
            case ALT: return "ALT";
            default: return keyCode.getName().toUpperCase();
        }
    }

    private void setupKeyCapture() {
        keyCapture.setOnKeyPressed(this::handleKeyCapture);
        keyCapture.setFocusTraversable(true);
    }

    private void handleKeyCapture(KeyEvent event) {
        if (currentKeyButton != null) {
            KeyCode newKey = event.getCode();

            // Vérifier si la touche n'est pas déjà utilisée
            if (isKeyAlreadyUsed(newKey, currentPlayer, currentAction)) {
                showKeyConflictDialog(newKey);
                return;
            }

            // Mettre à jour le bouton et les paramètres
            updateKeyButtonText(currentKeyButton, newKey);
            updateKeyBinding(currentPlayer, currentAction, newKey);

            hideKeyCapture();
        }
        event.consume();
    }

    private boolean isKeyAlreadyUsed(KeyCode keyCode, String excludePlayer, String excludeAction) {
        // Vérifier toutes les touches des deux joueurs
        return (settings.getPlayer1UpKey() == keyCode && !("player1".equals(excludePlayer) && "up".equals(excludeAction))) ||
                (settings.getPlayer1DownKey() == keyCode && !("player1".equals(excludePlayer) && "down".equals(excludeAction))) ||
                (settings.getPlayer1LeftKey() == keyCode && !("player1".equals(excludePlayer) && "left".equals(excludeAction))) ||
                (settings.getPlayer1RightKey() == keyCode && !("player1".equals(excludePlayer) && "right".equals(excludeAction))) ||
                (settings.getPlayer1BombKey() == keyCode && !("player1".equals(excludePlayer) && "bomb".equals(excludeAction))) ||
                (settings.getPlayer2UpKey() == keyCode && !("player2".equals(excludePlayer) && "up".equals(excludeAction))) ||
                (settings.getPlayer2DownKey() == keyCode && !("player2".equals(excludePlayer) && "down".equals(excludeAction))) ||
                (settings.getPlayer2LeftKey() == keyCode && !("player2".equals(excludePlayer) && "left".equals(excludeAction))) ||
                (settings.getPlayer2RightKey() == keyCode && !("player2".equals(excludePlayer) && "right".equals(excludeAction))) ||
                (settings.getPlayer2BombKey() == keyCode && !("player2".equals(excludePlayer) && "bomb".equals(excludeAction)));
    }

    private void showKeyConflictDialog(KeyCode conflictKey) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Conflit de touche");
        alert.setHeaderText("Touche déjà utilisée");
        alert.setContentText("La touche " + getKeyDisplayText(conflictKey) + " est déjà assignée à une autre action.\nVeuillez choisir une autre touche.");
        alert.showAndWait();
    }

    private void updateKeyBinding(String player, String action, KeyCode keyCode) {
        if ("player1".equals(player)) {
            switch (action) {
                case "up": settings.setPlayer1UpKey(keyCode); break;
                case "down": settings.setPlayer1DownKey(keyCode); break;
                case "left": settings.setPlayer1LeftKey(keyCode); break;
                case "right": settings.setPlayer1RightKey(keyCode); break;
                case "bomb": settings.setPlayer1BombKey(keyCode); break;
            }
        } else if ("player2".equals(player)) {
            switch (action) {
                case "up": settings.setPlayer2UpKey(keyCode); break;
                case "down": settings.setPlayer2DownKey(keyCode); break;
                case "left": settings.setPlayer2LeftKey(keyCode); break;
                case "right": settings.setPlayer2RightKey(keyCode); break;
                case "bomb": settings.setPlayer2BombKey(keyCode); break;
            }
        }
    }

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

    // Event Handlers for Player 1
    @FXML
    private void changePlayer1Up() {
        showKeyCapture(player1UpButton, "player1", "up");
    }

    @FXML
    private void changePlayer1Down() {
        showKeyCapture(player1DownButton, "player1", "down");
    }

    @FXML
    private void changePlayer1Left() {
        showKeyCapture(player1LeftButton, "player1", "left");
    }

    @FXML
    private void changePlayer1Right() {
        showKeyCapture(player1RightButton, "player1", "right");
    }

    @FXML
    private void changePlayer1Bomb() {
        showKeyCapture(player1BombButton, "player1", "bomb");
    }

    // Event Handlers for Player 2
    @FXML
    private void changePlayer2Up() {
        showKeyCapture(player2UpButton, "player2", "up");
    }

    @FXML
    private void changePlayer2Down() {
        showKeyCapture(player2DownButton, "player2", "down");
    }

    @FXML
    private void changePlayer2Left() {
        showKeyCapture(player2LeftButton, "player2", "left");
    }

    @FXML
    private void changePlayer2Right() {
        showKeyCapture(player2RightButton, "player2", "right");
    }

    @FXML
    private void changePlayer2Bomb() {
        showKeyCapture(player2BombButton, "player2", "bomb");
    }

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
            settings.resetToDefaults();
            loadCurrentSettings();
        }
    }

    @FXML
    private void applySettings() {
        // Sauvegarder dans un fichier
        settings.saveSettings();

        // Afficher confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paramètres sauvegardés");
        alert.setHeaderText("Succès");
        alert.setContentText("Les paramètres ont été sauvegardés avec succès !");
        alert.showAndWait();
    }

    @FXML
    private void cancelSettings() {
        // Recharger les paramètres depuis le fichier
        settings.loadSettings();
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