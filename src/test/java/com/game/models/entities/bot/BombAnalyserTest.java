package com.game.models.entities.bot;

import com.game.models.entities.Bomb;
import com.game.models.map.GameMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.layout.GridPane;

class BombAnalyzerTest {

    private GridPane mapGrid;
    private BombAnalyzer analyzer;
    private char[][] mapData;
    private GameMap mockMap;

    @BeforeEach
    void setUp() {
        // Exemple de carte :
        // '.' = vide, 'W' = mur, 'X' = bombe
        mapData = new char[][]{
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', 'X', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', 'W', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'}
        };

        mockMap = new GameMap();
        mockMap.setupMap(mapGrid);
        analyzer = new BombAnalyzer(mockMap);
    }

    @Test
    void testIsOnBomb() {
        assertTrue(analyzer.isOnBomb(1, 1));
        assertFalse(analyzer.isOnBomb(0, 0));
    }

    @Test
    void testIsWall() {
        assertTrue(analyzer.isWall(1, 3));
        assertFalse(analyzer.isWall(0, 0));
    }

    @Test
    void testIsValidPosition() {
        assertTrue(analyzer.isValidPosition(2, 2));
        assertFalse(analyzer.isValidPosition(-1, 0));
        assertFalse(analyzer.isValidPosition(5, 5));
    }

    @Test
    void testIsTraversable() {
        assertTrue(analyzer.isTraversable(0, 0));
        assertFalse(analyzer.isTraversable(1, 3)); // mur
    }

    @Test
    void testIsDangerousSimple() {
        assertTrue(analyzer.isDangerous(1, 2)); // à côté de bombe
        assertFalse(analyzer.isDangerous(4, 4)); // loin de bombe
    }

    @Test
    void testHasWallBetween() {
        assertTrue(analyzer.hasWallBetween(1, 1, 1, 4)); // mur entre (1,1) et (1,4)
        assertFalse(analyzer.hasWallBetween(1, 1, 1, 2)); // pas de mur
    }

    @Test
    void testDangerScoreWithPlacedBombs() {      
        //bomb = new Bomb(null, mapData, tiles, null, null, null); // null pour GridPane, à mocker si nécessaire
  
        Bomb bomb = new Bomb(null, mapData, null, null, null, null);
        PlacedBomb placedBomb = new PlacedBomb(1, 1, 3, 2.0); // position, portée, temps
        bomb.addPlacedBomb(placedBomb);
        analyzer.setBomb(bomb);

        int score = analyzer.getDangerScore(1, 2);
        assertTrue(score > 0, "La case (1,2) devrait être dangereuse");
    }
}
