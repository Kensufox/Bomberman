package com.game.controllers;

import com.game.utils.PlayerManager;
import com.game.utils.ScoreManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'intégration pour vérifier les interactions entre contrôleurs
 */
class ControleursIntegrationTest {

    @Test
    @DisplayName("Test flux complet : création profil -> sélection -> jeu")
    void testFluxCompletJeu() {
        
        // When - Création d'un profil
        PlayerManager.Player nouveauJoueur = PlayerManager.createPlayer("Integration", "Test", "Avatar");
        
        // Sélection du profil
        PlayerManager.setCurrentPlayer(nouveauJoueur);
        
        // Démarrage d'une partie
        PlayerManager.recordGamePlayed();

        // Then
        assertTrue(PlayerManager.hasCurrentPlayer(), "Un joueur doit être sélectionné");
        assertEquals(1, nouveauJoueur.getPartiesJouees(), "Une partie doit être enregistrée");
    }

    @Test
    @DisplayName("Test gestion des scores entre parties")
    void testGestionScores() {
        // Given
        ScoreManager.resetScores();

        // When
        ScoreManager.incrementP1Score();
        ScoreManager.incrementP1Score();
        ScoreManager.incrementP2Score();

        // Then
        assertEquals(2, ScoreManager.getP1Score(), "Joueur 1 doit avoir 2 points");
        assertEquals(1, ScoreManager.getP2Score(), "Joueur 2 doit avoir 1 point");
    }
}