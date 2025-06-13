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

    @Test
    void testFindImprovedEscapeMove_PrefersSafeDirection() {
        // Setup: Only one direction is safe and not dangerous
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(eq(2), eq(3))).thenReturn(false); // Right is safe
        when(bombAnalyzer.isDangerous(eq(1), eq(2))).thenReturn(true);  // Up is dangerous
        when(bombAnalyzer.isDangerous(eq(3), eq(2))).thenReturn(true);  // Down is dangerous
        when(bombAnalyzer.isDangerous(eq(2), eq(1))).thenReturn(true);  // Left is dangerous
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);
        when(bombAnalyzer.getMapData()).thenReturn(new char[5][5]);
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);

        int[] move = movementStrategy.findImprovedEscapeMove(2, 2, enemy);
        assertArrayEquals(new int[]{0, 1}, move); // Should move right
    }

    @Test
    void testFindImprovedEscapeMove_NoSafeMove_StaysInPlace() {
        // All directions are dangerous or invalid
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(false);

        int[] move = movementStrategy.findImprovedEscapeMove(2, 2, enemy);
        assertArrayEquals(new int[]{0, 0}, move);
    }

    @Test
    void testEvaluateMoveOption_ScoreCalculation() {
        // All safe, far from enemy, open area
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);
        when(bombAnalyzer.getMapData()).thenReturn(new char[10][10]);
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);

        // Use reflection to access private method
        int[] direction = {1, 0};
        try {
            java.lang.reflect.Method method = MovementStrategy.class.getDeclaredMethod(
                    "evaluateMoveOption", int.class, int.class, Player.class, int[].class);
            method.setAccessible(true);
            Object moveOption = method.invoke(movementStrategy, 5, 5, enemy, direction);
            assertNotNull(moveOption);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void testIsNearEdge_TrueAndFalse() {
        char[][] map = new char[5][5];
        when(bombAnalyzer.getMapData()).thenReturn(map);

        // Use reflection to access private method
        try {
            java.lang.reflect.Method method = MovementStrategy.class.getDeclaredMethod(
                    "isNearEdge", int.class, int.class);
            method.setAccessible(true);
            // Near edge
            assertTrue((Boolean) method.invoke(movementStrategy, 1, 2));
            assertTrue((Boolean) method.invoke(movementStrategy, 2, 1));
            assertTrue((Boolean) method.invoke(movementStrategy, 3, 4));
            // Not near edge
            assertFalse((Boolean) method.invoke(movementStrategy, 2, 2));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    void testFindSafeMoveTowardsEnemy_PrefersCloserAndSafe() {
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getMapData()).thenReturn(new char[5][5]);
        when(enemy.getRow()).thenReturn(2);
        when(enemy.getCol()).thenReturn(3);

        int[] move = movementStrategy.calculateOptimalMove(2, 2, enemy);
        // Should move not right towards enemy
        //should go away
        assertNotEquals(0, move[0]);
        assertNotEquals(1, move[1]);
    }

    @Test
    void testCanEscapeAfterBomb_NoEscape() {
        char[][] map = new char[3][3];
        when(bombAnalyzer.getMapData()).thenReturn(map);
        // Only current position is valid, all others are not
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.isValidPosition(eq(1), eq(1))).thenReturn(true);
        when(bombAnalyzer.isTraversable(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.isTraversable(eq(1), eq(1))).thenReturn(true);
        when(bombAnalyzer.isDangerous(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(10);
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);

        boolean result = movementStrategy.canEscapeAfterBomb(1, 1, enemy);
        assertFalse(result);
    }

    @Test
    void testIsEnemyInBombRange_DifferentRowCol() {
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);
        mockStaticBombRange(3);

        boolean result = movementStrategy.isEnemyInBombRange(2, 2, enemy);
        assertFalse(result);
    }

    @Test
    void testShouldPlaceBomb_EnemyNotInRange() {
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);
        mockStaticBombRange(3);

        long now = 10_000_000_000L;
        long last = 0L;
        boolean result = movementStrategy.shouldPlaceBomb(2, 2, enemy, now, last);
        assertFalse(result);
    }
}