package com.game.models.entities.bot;

import com.game.models.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;






class MovementStrategyTest {

    private BombAnalyzer bombAnalyzer;
    private PathFinder pathFinder;
    private MovementStrategy movementStrategy;
    private Player enemy;

    @BeforeEach
    void setUp() {
        bombAnalyzer = mock(BombAnalyzer.class);
        pathFinder = mock(PathFinder.class);
        movementStrategy = new MovementStrategy(bombAnalyzer, pathFinder);
        enemy = mock(Player.class);
    }

    @Test
    void testCalculateOptimalMove_FleeDanger() {
        when(bombAnalyzer.isDangerous(2, 2)).thenReturn(true);
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);
        when(bombAnalyzer.getMapData()).thenReturn(new char[5][5]);
        when(enemy.getRow()).thenReturn(4);
        when(enemy.getCol()).thenReturn(4);

        int[] move = movementStrategy.calculateOptimalMove(2, 2, enemy);
        assertNotNull(move);
        assertEquals(2, move.length);
        // Should not stay in place if there is a valid escape
        assertFalse(move[0] == 0 && move[1] == 0);
    }

    @Test
    void testCalculateOptimalMove_UseAStar() {
        when(bombAnalyzer.isDangerous(1, 1)).thenReturn(false);
        when(enemy.getRow()).thenReturn(3);
        when(enemy.getCol()).thenReturn(1);

        Node start = new Node(1, 1, 0, 0);
        Node next = new Node(2, 1, 0, 0);
        List<Node> path = Arrays.asList(start, next, new Node(3, 1, 0, 0));
        when(pathFinder.findPathToTarget(1, 1, 3, 1)).thenReturn(path);

        int[] move = movementStrategy.calculateOptimalMove(1, 1, enemy);
        assertArrayEquals(new int[]{1, 0}, move);
    }

    @Test
    void testCalculateOptimalMove_SafeMoveTowardsEnemy() {
        when(bombAnalyzer.isDangerous(1, 1)).thenReturn(false);
        when(enemy.getRow()).thenReturn(1);
        when(enemy.getCol()).thenReturn(3);
        when(pathFinder.findPathToTarget(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(null);
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getMapData()).thenReturn(new char[5][5]);

        int[] move = movementStrategy.calculateOptimalMove(1, 1, enemy);
        assertNotNull(move);
        assertEquals(2, move.length);
    }

    @Test
    void testCountEscapeRoutes_AllDirectionsOpen() {
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);

        int routes = movementStrategy.countEscapeRoutes(2, 2);
        assertEquals(4, routes);
    }

    @Test
    void testCountEscapeRoutes_NoEscape() {
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(false);

        int routes = movementStrategy.countEscapeRoutes(0, 0);
        assertEquals(0, routes);
    }

    @Test
    void testIsEnemyInBombRange_SameRow_NoWall() {
        when(enemy.getRow()).thenReturn(2);
        when(enemy.getCol()).thenReturn(4);
        when(bombAnalyzer.hasWallBetween(2, 2, 2, 4)).thenReturn(false);

        boolean result = movementStrategy.isEnemyInBombRange(2, 2, enemy);
        assertTrue(result);
    }

    @Test
    void testIsEnemyInBombRange_SameCol_WithWall() {
        when(enemy.getRow()).thenReturn(4);
        when(enemy.getCol()).thenReturn(2);
        when(bombAnalyzer.hasWallBetween(1, 2, 4, 2)).thenReturn(true);
        mockStaticBombRange(3);

        boolean result = movementStrategy.isEnemyInBombRange(1, 2, enemy);
        assertFalse(result);
    }

    @Test
    void testShouldPlaceBomb_AllConditionsMet() {
        when(enemy.getRow()).thenReturn(2);
        when(enemy.getCol()).thenReturn(4);
        when(bombAnalyzer.hasWallBetween(2, 2, 2, 4)).thenReturn(false);
        when(bombAnalyzer.getMapData()).thenReturn(new char[5][5]);
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);
        mockStaticBombRange(3);

        long now = 10_000_000_000L;
        long last = 0L;
        boolean result = movementStrategy.shouldPlaceBomb(2, 2, enemy, now, last);
        assertTrue(result);
    }

    @Test
    void testShouldPlaceBomb_CooldownNotMet() {
        when(enemy.getRow()).thenReturn(2);
        when(enemy.getCol()).thenReturn(4);
        mockStaticBombRange(3);

        long now = 1_000_000_000L;
        long last = 0L;
        boolean result = movementStrategy.shouldPlaceBomb(2, 1, enemy, now, last);
        assertFalse(result);
    }

    @Test
    void testCanEscapeAfterBomb_EscapePossible() {
        char[][] map = new char[5][5];
        when(bombAnalyzer.getMapData()).thenReturn(map);
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);
        when(enemy.getRow()).thenReturn(4);
        when(enemy.getCol()).thenReturn(4);

        boolean result = movementStrategy.canEscapeAfterBomb(2, 2, enemy);
        assertTrue(result);
    }

    @Test
    void testManhattanDistance() {
        assertEquals(4, movementStrategy.manhattanDistance(1, 1, 3, 3));
        assertEquals(0, movementStrategy.manhattanDistance(2, 2, 2, 2));
        assertEquals(5, movementStrategy.manhattanDistance(0, 0, 2, 3));
    }

    // Helper to mock Bomb.getOriginalRange() static method
    private void mockStaticBombRange(int range) {
        // Bomb.getOriginalRange() is static, so we need to mock it.
        // If using Mockito 4+ with mockito-inline, you can do:
        // try (MockedStatic<Bomb> bombMock = Mockito.mockStatic(Bomb.class)) {
        //     bombMock.when(Bomb::getOriginalRange).thenReturn(range);
        // }
        // But for this test class, we assume Bomb.getOriginalRange() returns 3 by default.
    }
}