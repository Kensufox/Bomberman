package com.game.models.entities.bot;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.game.models.entities.Bomb;
import com.game.models.map.GameMap;

public class BombAnalyzerTest {

    private BombAnalyzer analyzer;
    private GameMap mockMap;
    private Bomb mockBomb;

    @BeforeEach
    void setUp() {
        // Création d'une carte manuellement
        char[][] mapData = new char[][]{
            {'.', '.', '.', '.', '.'},
            {'.', 'X', '.', 'W', '.'},
            {'.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.'}
        };

        mockMap = new GameMap() {
            @Override
            public char[][] getMapData() {
                return mapData;
            }
        };

        // Bombes actives fictives
        List<PlacedBomb> bombs = new ArrayList<>();
        bombs.add(new PlacedBomb(1, 1, 2, 3));  // Bombe à (1,1), portée 2, explose dans 3 ticks

        mockBomb = new Bomb(null, mapData, null, null, null, null) {
            @Override
            public List<PlacedBomb> getActiveBombs() {
                return bombs;
            }
        };

        analyzer = new BombAnalyzer(mockMap);
        analyzer.setBomb(mockBomb);
    }

    @Test
    void testIsDangerous() {
        // Directement sur la bombe
        assertTrue(analyzer.isDangerous(1, 1));
        // Sur la ligne horizontale (à droite de la bombe) jusqu'au mur
        assertTrue(analyzer.isDangerous(1, 2));
        assertFalse(analyzer.isDangerous(1, 3)); // Est un mur

        // Ligne verticale
        assertTrue(analyzer.isDangerous(0, 1));
        assertTrue(analyzer.isDangerous(2, 1));
        assertFalse(analyzer.isDangerous(3, 1)); // hors de portée
    }

    @Test
    void testDangerScore() {
        // Bombe
        assertEquals(100 + 50 + 150, analyzer.getDangerScore(1, 1)); // 100 (sur bombe) + 50 + 20
        // Adjacent à la bombe
        assertTrue(analyzer.getDangerScore(1, 2) > 0);
        assertEquals(0, analyzer.getDangerScore(4, 4)); // loin, pas de danger
    }

    @Test
    void testHasWallBetween() {
        assertTrue(analyzer.hasWallBetween(1, 1, 1, 4)); // mur à (1,3)
        assertFalse(analyzer.hasWallBetween(1, 1, 1, 2)); // pas de mur entre (1,1) et (1,2)
    }
}
