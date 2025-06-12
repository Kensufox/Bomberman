package com.game.controllers;

import com.game.utils.PlayerManager;
import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Controller for creating new player profiles.
 * Handles input validation, avatar selection, and profile persistence.
 * @author RADJOU Dinesh G2-5
 * @version 1.1
 * @since 2025-06-10
 */
public class NouveauProfilController {

    /** Text field for entering the player's last name */
    @FXML private TextField champNom;
    /** Text field for entering the player's first name */
    @FXML private TextField champPrenom;
    /** Dropdown list to choose an avatar */
    @FXML private ComboBox<String> choixAvatar;
    /** Label to display error messages to the user */
    @FXML private Label messageErreur;
    /** Button to trigger profile creation */
    @FXML private Button btnCreer;
    /** Button to cancel profile creation and return to the previous screen */
    @FXML private Button btnAnnuler;


    /**
     * Initializes the controller by setting up UI elements,
     * including default avatar selection and sound effects.
     */
    @FXML
    public void initialize() {
        SFXPlayer.setupHoverSound(btnCreer);
        SFXPlayer.setupHoverSound(btnAnnuler);

        // Sélectionner le premier avatar par défaut
        if (!choixAvatar.getItems().isEmpty()) {
            choixAvatar.getSelectionModel().selectFirst();
        }
    }

    /**
     * Called when the "Create Profile" button is clicked.
     * Validates input fields, creates the player profile, and navigates back to the profile menu.
     *
     * @param event the ActionEvent triggered by the button
     */
    @FXML
    public void creerProfil(ActionEvent event) {
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String avatar = choixAvatar.getSelectionModel().getSelectedItem();

        // Validation
        if (nom.isEmpty() || prenom.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (avatar == null) {
            avatar = "Bomberman Bleu"; // Avatar par défaut
        }

        // Vérifier si le profil existe déjà
        if (PlayerManager.findPlayer(nom, prenom) != null) {
            afficherErreur("Un profil avec ce nom et prénom existe déjà.");
            return;
        }

        try {
            // Créer le profil (automatiquement sauvegardé)
            PlayerManager.Player nouveauJoueur = PlayerManager.createPlayer(nom, prenom, avatar);
            PlayerManager.setCurrentPlayer(nouveauJoueur);

            SFXPlayer.play(SFXLibrary.FINISH);

            // Retourner à l'écran des profils
            retourProfils(event);

        } catch (Exception e) {
            afficherErreur("Erreur lors de la création du profil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the "Cancel" button is clicked.
     * Returns the user to the profile selection screen.
     *
     * @param event the ActionEvent triggered by the button
     */
    @FXML
    public void annuler(ActionEvent event) {
        SFXPlayer.play(SFXLibrary.CANCEL);
        retourProfils(event);
    }

    /**
     * Navigates back to the profile selection screen.
     *
     * @param event the ActionEvent triggering the navigation
     */
    private void retourProfils(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profils_menu.fxml"));
            AnchorPane root = loader.load();

            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Displays an error message in the error label.
     *
     * @param message the message to display
     */
    private void afficherErreur(String message) {
        messageErreur.setText(message);
        messageErreur.setVisible(true);
    }
}