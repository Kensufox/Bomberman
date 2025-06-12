package com.game.models.entities.bot;

import com.game.models.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void testFindPathToTarget_StartEqualsTarget() {
        // Si départ = arrivée, le chemin doit contenir un seul nœud
        when(bombAnalyzer.isTraversable(1, 1)).thenReturn(true);
        when(bombAnalyzer.isDangerous(1, 1)).thenReturn(false);

        List<Node> path = pathFinder.findPathToTarget(1, 1, 1, 1);

        assertNotNull(path);
        assertEquals(1, path.size());
        assertEquals(1, path.get(0).row);
        assertEquals(1, path.get(0).col);
    }

    @Test
    void testFindPathToTarget_TargetNotTraversable() {
        // La cible n'est pas traversable
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }
        when(bombAnalyzer.isTraversable(2, 2)).thenReturn(false);

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNull(path);
    }

    @Test
    void testFindPathToTarget_StartNotTraversable() {
        // Le départ n'est pas traversable
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }
        when(bombAnalyzer.isTraversable(0, 0)).thenReturn(false);

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNull(path);
    }

    @Test
    void testFindPathToTarget_PathAvoidsWallsAndBombs() {
        // (1,1) est un mur, (2,1) est une bombe
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                when(bombAnalyzer.isTraversable(r, c)).thenReturn(true);
                when(bombAnalyzer.isDangerous(r, c)).thenReturn(false);
            }
        when(bombAnalyzer.isTraversable(1, 1)).thenReturn(false); // wall
        when(bombAnalyzer.isTraversable(2, 1)).thenReturn(false); // bomb

        List<Node> path = pathFinder.findPathToTarget(0, 0, 2, 2);

        assertNotNull(path);
        // Le chemin ne doit pas passer par (1,1) ni (2,1)
        for (Node node : path) {
            assertFalse((node.row == 1 && node.col == 1) || (node.row == 2 && node.col == 1));
        }
    }

    
}