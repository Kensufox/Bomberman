package com.game.controllers;

import java.io.IOException;

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

/**
 * Controller for creating new player profiles.
 */
public class NouveauProfilController {

    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private ComboBox<String> choixAvatar;
    @FXML private Label messageErreur;
    @FXML private Button btnCreer;
    @FXML private Button btnAnnuler;

    @FXML
    public void initialize() {
        SFXPlayer.setupHoverSound(btnCreer);
        SFXPlayer.setupHoverSound(btnAnnuler);

        // Sélectionner le premier avatar par défaut
        if (!choixAvatar.getItems().isEmpty()) {
            choixAvatar.getSelectionModel().selectFirst();
        }
    }

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

    @FXML
    public void annuler(ActionEvent event) {
        SFXPlayer.play(SFXLibrary.CANCEL);
        retourProfils(event);
    }

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

    private void afficherErreur(String message) {
        messageErreur.setText(message);
        messageErreur.setVisible(true);
    }
}