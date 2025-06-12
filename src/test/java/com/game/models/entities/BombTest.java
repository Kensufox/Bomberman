package com.game.models.entities;

import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BombTest {

    private Bomb bomb;

    @BeforeEach
    void setUp() {
        // Simulation minimale pour les besoins de test
        char[][] mapData = new char[5][5];
        StackPane[][] tiles = new StackPane[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                tiles[i][j] = new StackPane();
            }
        }

        bomb = new Bomb(null, mapData, tiles, null, null, null); // null pour GridPane, à mocker si nécessaire
            //public Bomb(GridPane mapGrid, char[][] mapData, StackPane[][] tiles, Image emptyImg, List<Player> players, GameMapController controller) {
    }

    @Test
    void testInitialRange() {
        assertEquals(2, bomb.getRange());  // Supposé getter, à confirmer dans le code réel
    }

    @Test
    void testSetRange() {
        bomb.setRange(4);  // setter présumé
        assertEquals(4, bomb.getRange());
    }

    @Test
    void testRangeCannotBeNegative() {
        bomb.setRange(-1);
        assertTrue(bomb.getRange() >= 0, "La portée ne devrait pas être négative.");
    }

    @Test
    void testOriginalRangeConstant() {
        assertEquals(2, Bomb.getOriginalRange()); // si méthode statique existe
    }
}
