package com.game.models.entities.bot;

import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PathFinderTest {

    private GameMap gameMap;
    private BombAnalyzer bombAnalyzer;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        gameMap = mock(GameMap.class);
        bombAnalyzer = mock(BombAnalyzer.class);
        pathFinder = new PathFinder(gameMap, bombAnalyzer);
    }

    @Test
    void testFindPathToTarget_StraightLine() {
        // 3x3 grid, all traversable and safe
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNotNull(path);
        assertEquals(5, path.size()); // (0,0) -> (0,1) -> (0,2) -> (1,2) -> (2,2) or similar
        assertEquals(0, path.get(0).row);
        assertEquals(0, path.get(0).col);
        assertEquals(2, path.get(path.size() - 1).row);
        assertEquals(2, path.get(path.size() - 1).col);
    }

    @Test
    void testFindPathToTarget_NoPath() {
        // Block the middle cell
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }
        when(bombAnalyzer.isTraversable(1, 0)).thenReturn(false);
        when(bombAnalyzer.isTraversable(0, 1)).thenReturn(false);

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNull(path);
    }

    @Test
    void testFindPathToTarget_AvoidDanger() {
        // All traversable, but (1,1) is dangerous
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }
        when(bombAnalyzer.isDangerous(1, 1)).thenReturn(true);

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNotNull(path);
        // Path should not include (1,1)
        for (Node node : path) {
            assertFalse(node.row == 1 && node.col == 1);
        }
    }

    @Test
    void testFindSafeDirection_SafeMoveAvailable() {
        // Setup: (1,1) is start, (0,1) is safe, enemy at (2,2)
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isWall(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.isOnBomb(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.getDangerScore(anyInt(), anyInt())).thenReturn(0);

        Player enemy = mock(Player.class);
        when(enemy.getRow()).thenReturn(2);
        when(enemy.getCol()).thenReturn(2);

        int[] dir = pathFinder.findSafeDirection(1, 1, enemy, 3);

        assertNotNull(dir);
        // Should be one of the four directions
        boolean valid = (dir[0] == -1 && dir[1] == 0) ||
                        (dir[0] == 1 && dir[1] == 0) ||
                        (dir[0] == 0 && dir[1] == -1) ||
                        (dir[0] == 0 && dir[1] == 1);
        assertTrue(valid);
    }

    @Test
    void testFindSafeDirection_NoSafeMove() {
        // All neighbors are walls or on bombs
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isWall(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isOnBomb(anyInt(), anyInt())).thenReturn(true);

        Player enemy = mock(Player.class);
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);

        int[] dir = pathFinder.findSafeDirection(1, 1, enemy, 2);

        assertNull(dir);
    }

    @Test
    void testFindSafeDirection_PrefersLessDanger() {
        // (1,2) is less dangerous than (1,0)
        when(bombAnalyzer.isValidPosition(anyInt(), anyInt())).thenReturn(true);
        when(bombAnalyzer.isWall(anyInt(), anyInt())).thenReturn(false);
        when(bombAnalyzer.isOnBomb(anyInt(), anyInt())).thenReturn(false);

        when(bombAnalyzer.getDangerScore(1, 0)).thenReturn(5);
        when(bombAnalyzer.getDangerScore(1, 2)).thenReturn(1);
        when(bombAnalyzer.getDangerScore(0, 1)).thenReturn(0);
        when(bombAnalyzer.getDangerScore(2, 1)).thenReturn(0);

        Player enemy = mock(Player.class);
        when(enemy.getRow()).thenReturn(0);
        when(enemy.getCol()).thenReturn(0);

        int[] dir = pathFinder.findSafeDirection(1, 1, enemy, 2);

        // Should pick a direction with dangerScore 0 if available
        assertNotNull(dir);
        boolean isZeroDanger = (dir[0] == -1 && dir[1] == 0) || (dir[0] == 1 && dir[1] == 0);
        assertTrue(isZeroDanger);
    }
}