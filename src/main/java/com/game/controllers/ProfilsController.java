package com.game.controllers;

import java.io.IOException;
import java.util.List;

import com.game.utils.PlayerManager;
import com.game.utils.PlayerManager.Player;
import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for managing player profiles.
 */
public class ProfilsController {

    @FXML private ListView<Player> listeProfils;
    @FXML private Label nomJoueur;
    @FXML private Label prenomJoueur;
    @FXML private Label partiesJouees;
    @FXML private Label partiesGagnees;
    @FXML private Label ratioVictoires;
    @FXML private Button btnNouveauProfil;
    @FXML private Button btnSelectionner;
    @FXML private Button btnSupprimer;
    @FXML private Button btnRetourMenu;

    @FXML
    public void initialize() {
        SFXPlayer.setupHoverSound(btnNouveauProfil);
        SFXPlayer.setupHoverSound(btnSelectionner);
        SFXPlayer.setupHoverSound(btnSupprimer);
        SFXPlayer.setupHoverSound(btnRetourMenu);

        chargerProfils();

        // Listener pour afficher les détails du profil sélectionné
        listeProfils.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherDetailsProfil(newSelection);
                    }
                });
    }

    private void chargerProfils() {
        List<Player> profils = PlayerManager.getAllPlayers();
        listeProfils.getItems().setAll(profils);
    }

    private void afficherDetailsProfil(Player player) {
        nomJoueur.setText(player.getNom());
        prenomJoueur.setText(player.getPrenom());
        partiesJouees.setText(String.valueOf(player.getPartiesJouees()));
        partiesGagnees.setText(String.valueOf(player.getPartiesGagnees()));
        ratioVictoires.setText(String.format("%.1f%%", player.getRatioVictoires()));
    }

    @FXML
    public void nouveauProfil(ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.FINISH);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nouveau_profil.fxml"));
            AnchorPane root = loader.load();

            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectionnerProfil(ActionEvent event) {
        Player selectedPlayer = listeProfils.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            PlayerManager.setCurrentPlayer(selectedPlayer);
            SFXPlayer.play(SFXLibrary.FINISH);
            retourMenu(event);
        }
    }

    @FXML
    public void supprimerProfil(ActionEvent event) {
        Player selectedPlayer = listeProfils.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            PlayerManager.deletePlayer(selectedPlayer);
            chargerProfils();
            // Effacer les détails affichés
            nomJoueur.setText("");
            prenomJoueur.setText("");
            partiesJouees.setText("");
            partiesGagnees.setText("");
            ratioVictoires.setText("");
        }
    }

    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            SFXPlayer.play(SFXLibrary.CANCEL);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            AnchorPane root = loader.load();

            Button sourceButton = (Button) event.getSource();
            sourceButton.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}