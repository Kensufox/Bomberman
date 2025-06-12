package com.game.controllers;

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

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing player profiles.
 * Allows users to create, select, delete, and view statistics of player profiles.
 * @author RADJOU Dinesh G2-5
 * @version 1.1
 * @since 2025-06-10
 */
public class ProfilsController {

    /** List view displaying available player profiles */
    @FXML private ListView<Player> listeProfils;
    /** Label displaying the selected player's last name */
    @FXML private Label nomJoueur;
    /** Label displaying the selected player's first name */
    @FXML private Label prenomJoueur;
    /** Label displaying the number of games played by the selected player */
    @FXML private Label partiesJouees;
    /** Label displaying the number of games won by the selected player */
    @FXML private Label partiesGagnees;
    /** Label displaying the win ratio of the selected player */
    @FXML private Label ratioVictoires;
    /** Button to navigate to the new profile creation screen */
    @FXML private Button btnNouveauProfil;
    /** Button to select the highlighted profile and continue */
    @FXML private Button btnSelectionner;
    /** Button to delete the selected profile */
    @FXML private Button btnSupprimer;
    /** Button to return to the main menu */
    @FXML private Button btnRetourMenu;

    /**
     * Initializes the controller by loading existing profiles, setting up UI event listeners,
     * and applying hover sound effects to buttons.
     */
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

    /**
     * Loads all existing player profiles from the data source into the list view.
     */
    private void chargerProfils() {
        List<Player> profils = PlayerManager.getAllPlayers();
        listeProfils.getItems().setAll(profils);
    }

    /**
     * Displays detailed information about the selected player in the appropriate labels.
     *
     * @param player the selected player whose details are to be displayed
     */
    private void afficherDetailsProfil(Player player) {
        nomJoueur.setText(player.getNom());
        prenomJoueur.setText(player.getPrenom());
        partiesJouees.setText(String.valueOf(player.getPartiesJouees()));
        partiesGagnees.setText(String.valueOf(player.getPartiesGagnees()));
        ratioVictoires.setText(String.format("%.1f%%", player.getRatioVictoires()));
    }

    /**
     * Navigates to the profile creation screen when the "New Profile" button is clicked.
     *
     * @param event the ActionEvent triggered by the button
     */
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

    /**
     * Sets the selected profile as the current player and navigates back to the main menu.
     *
     * @param event the ActionEvent triggered by the button
     */
    @FXML
    public void selectionnerProfil(ActionEvent event) {
        Player selectedPlayer = listeProfils.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            PlayerManager.setCurrentPlayer(selectedPlayer);
            SFXPlayer.play(SFXLibrary.FINISH);
            retourMenu(event);
        }
    }

    /**
     * Deletes the selected profile and updates the UI.
     *
     * @param event the ActionEvent triggered by the button
     */
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

    /**
     * Returns to the main menu screen.
     *
     * @param event the ActionEvent triggered by the button
     */
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