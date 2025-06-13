package com.game.models.entities.bot;

import com.game.models.entities.Bomb;
import com.game.models.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        bombs.add(new PlacedBomb(1, 1, 2, 2));  // Bombe à (1,1), portée 2, explose dans 2s

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
        assertTrue(analyzer.isDangerous(4, 1)); //le bot prends en compte toujour une case en plus
        assertFalse(analyzer.isDangerous(5, 1)); // hors de portée
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

    @Test
    void testIsOnBomb() {
        assertTrue(analyzer.isOnBomb(1, 1));
        assertFalse(analyzer.isOnBomb(0, 0));
    }

    @Test
    void testIsWall() {
        assertTrue(analyzer.isWall(1, 3));
        assertFalse(analyzer.isWall(1, 2));
        assertFalse(analyzer.isWall(-1, 0)); // hors carte
    }

    @Test
    void testIsValidPosition() {
        assertTrue(analyzer.isValidPosition(0, 0));
        assertFalse(analyzer.isValidPosition(-1, 0));
        assertFalse(analyzer.isValidPosition(5, 0));
        assertFalse(analyzer.isValidPosition(0, 5));
    }

    @Test
    void testIsTraversable() {
        assertTrue(analyzer.isTraversable(0, 0));
        assertFalse(analyzer.isTraversable(1, 1)); // bombe
        assertFalse(analyzer.isTraversable(1, 3)); // mur
        assertFalse(analyzer.isTraversable(-1, 0)); // hors carte
    }

    @Test
    void testGetMapDataReturnsSameReference() {
        char[][] mapData = analyzer.getMapData();
        assertEquals('.', mapData[0][0]);
        assertEquals('W', mapData[1][3]);
    }

    @Test
    void testIsDangerousReturnsFalseForInvalidOrWall() {
        assertFalse(analyzer.isDangerous(-1, 0));
        assertFalse(analyzer.isDangerous(1, 3)); // mur
    }

    @Test
    void testGetDangerScoreNoBombs() {
        // Retire toutes les bombes
        analyzer.setBomb(new Bomb(null, mockMap.getMapData(), null, null, null, null) {
            @Override
            public java.util.List<PlacedBomb> getActiveBombs() {
                return new java.util.ArrayList<>();
            }
        });
        assertEquals(0, analyzer.getDangerScore(0, 0));
    }

    @Test
    void testSetBombNullSafe() {
        analyzer.setBomb(null);
        // Ne doit pas lever d'exception même si bomb est null
        assertFalse(analyzer.isDangerous(0, 0));
    }

    @Test
    void testIsInExplosionRange() {
        // Bombe à (1,1), portée 2+1=3 (BOMB_RANGE)
        // Même ligne, dans la portée
        assertTrue(analyzer.isInExplosionRange(1, 2, 1, 1));
        assertTrue(analyzer.isInExplosionRange(1, 3, 1, 1)); // mur, mais isInExplosionRange doit vérifier le mur
        // Même colonne, dans la portée
        assertTrue(analyzer.isInExplosionRange(2, 1, 1, 1));
        assertTrue(analyzer.isInExplosionRange(4, 1, 1, 1));
        // Hors portée
        assertFalse(analyzer.isInExplosionRange(1, 5, 1, 1));
        assertFalse(analyzer.isInExplosionRange(5, 1, 1, 1));
        // Pas sur la même ligne/colonne
        assertFalse(analyzer.isInExplosionRange(2, 2, 1, 1));
    }

    @Test
    void testCheckWallsInRangeHorizontal() {
        // (1,1) à (1,4) : mur à (1,3)
        assertTrue(analyzer.hasWallBetween(1, 1, 1, 4));
        // (1,1) à (1,2) : pas de mur
        assertFalse(analyzer.hasWallBetween(1, 1, 1, 2));
    }

    @Test
    void testCheckWallsInRangeVertical() {
        // (0,1) à (4,1) : pas de mur
        assertFalse(analyzer.hasWallBetween(0, 1, 4, 1));
    }

    @Test
    void testGetBombAndSetBomb() {
        Bomb anotherBomb = new Bomb(null, mockMap.getMapData(), null, null, null, null);
        analyzer.setBomb(anotherBomb);
        assertEquals(anotherBomb, analyzer.getBomb());
    }

    @Test
    void testIsWallWithInvalidPosition() {
        assertFalse(analyzer.isWall(-1, -1));
        assertFalse(analyzer.isWall(100, 100));
    }

    @Test
    void testIsOnBombWithInvalidPosition() {
        // Should throw ArrayIndexOutOfBoundsException if not checked, but our code does not check
        // So we expect an exception here
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> analyzer.isOnBomb(-1, -1));
    }

    @Test
    void testIsTraversableWithBombAndWall() {
        assertFalse(analyzer.isTraversable(1, 1)); // bomb
        assertFalse(analyzer.isTraversable(1, 3)); // wall
    }


    @Test
    void testGetDangerScoreWithMultipleBombs() {
        // Add another bomb at (2,2)
        List<PlacedBomb> bombs = new ArrayList<>(mockBomb.getActiveBombs());
        bombs.add(new PlacedBomb(2, 2, 2, 1));
        Bomb multiBomb = new Bomb(null, mockMap.getMapData(), null, null, null, null) {
            @Override
            public List<PlacedBomb> getActiveBombs() {
                return bombs;
            }
        };
        analyzer.setBomb(multiBomb);
        // (2,2) is on a bomb
        assertTrue(analyzer.getDangerScore(2, 2) >= 100);
        // (1,1) is also on a bomb
        assertTrue(analyzer.getDangerScore(1, 1) >= 100);
    }

    @Test
    void testIsDangerousWithNoBombs() {
        analyzer.setBomb(new Bomb(null, mockMap.getMapData(), null, null, null, null) {
            @Override
            public List<PlacedBomb> getActiveBombs() {
                return null;
            }
        });
        assertFalse(analyzer.isDangerous(1, 1));
    }

    @Test
    void testIsDangerousWithBombNextToWall() {
        // Place a bomb on a wall (should not happen in game, but test logic)
        List<PlacedBomb> bombs = new ArrayList<>();
        bombs.add(new PlacedBomb(1, 2, 2, 2)); // (1,3) is a wall
        Bomb wallBomb = new Bomb(null, mockMap.getMapData(), null, null, null, null) {
            @Override
            public List<PlacedBomb> getActiveBombs() {
                return bombs;
            }
        };
        analyzer.setBomb(wallBomb);
        // Should not be dangerous
        assertFalse(analyzer.isDangerous(1, 4));
    }
}
