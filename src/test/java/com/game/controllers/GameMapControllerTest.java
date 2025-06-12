package com.game.controllers;

import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import com.game.utils.InputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;




class GameMapControllerTest {

    private GameMapController controller;

    @BeforeEach
    void setUp() {
        controller = new GameMapController();
        controller.mapGrid = new GridPane();
        controller.backgroundGrid = new GridPane();
        controller.inputHandler = mock(InputHandler.class);
        controller.gameMap = mock(GameMap.class);

        // Mock map data: 15x15 grid, all walkable ('.')
        char[][] mapData = new char[15][15];
        for (char[] row : mapData) {
            java.util.Arrays.fill(row, '.');
        }
        when(controller.gameMap.getMapData()).thenReturn(mapData);
        when(controller.gameMap.getTileSize()).thenReturn(32);
    }

    @Test
    void testCreatePlayers() {
        Player[] players = controller.createPlayers();
        assertEquals(2, players.length);
        assertEquals(1, players[0].getCol());
        assertEquals(1, players[0].getRow());
        assertEquals(13, players[1].getCol());
        assertEquals(11, players[1].getRow());
        assertEquals(Player.State.ALIVE, players[0].getState());
        assertEquals(Player.State.ALIVE, players[1].getState());
    }

    @Test
    void testIsWalkableReturnsTrueForDotOrP() {
        char[][] mapData = controller.gameMap.getMapData();
        mapData[2][3] = '.';
        assertTrue(controller.isWalkable(2, 3));
        mapData[2][3] = 'P';
        assertTrue(controller.isWalkable(2, 3));
    }

    @Test
    void testIsWalkableReturnsFalseForOtherChars() {
        char[][] mapData = controller.gameMap.getMapData();
        mapData[2][3] = '#';
        assertFalse(controller.isWalkable(2, 3));
    }

    @Test
    void testIsWalkableReturnsFalseForOutOfBounds() {
        assertFalse(controller.isWalkable(-1, 0));
        assertFalse(controller.isWalkable(0, -1));
        assertFalse(controller.isWalkable(100, 0));
        assertFalse(controller.isWalkable(0, 100));
    }

    @Test
    void testMovePlayerIfPossibleMovesPlayerWhenWalkable() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        StackPane cell = new StackPane();
        controller.mapGrid.add(cell, 1, 1);

        controller.movePlayerIfPossible(player, cell, 1, 0);

        assertEquals(2, player.getRow());
        assertEquals(1, player.getCol());
        assertEquals(2, GridPane.getRowIndex(cell));
        assertEquals(1, GridPane.getColumnIndex(cell));
    }

    @Test
    void testMovePlayerIfPossibleDoesNotMoveWhenBlocked() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        StackPane cell = new StackPane();
        controller.mapGrid.add(cell, 1, 1);

        // Block the cell
        controller.gameMap.getMapData()[2][1] = '#';

        controller.movePlayerIfPossible(player, cell, 1, 0);

        assertEquals(1, player.getRow());
        assertEquals(1, player.getCol());
        assertEquals(1, GridPane.getRowIndex(cell));
        assertEquals(1, GridPane.getColumnIndex(cell));
    }

    @Test
    void testHandleKeyPressedAddsKeyToPressedKeys() {
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.A);

        controller.handleKeyPressed(event);

        assertTrue(controller.pressedKeys.contains(KeyCode.A));
    }

    @Test
    void testHandleKeyReleasedRemovesKeyFromPressedKeys() {
        controller.pressedKeys.add(KeyCode.B);
        KeyEvent event = mock(KeyEvent.class);
        when(event.getCode()).thenReturn(KeyCode.B);

        controller.handleKeyReleased(event);

        assertFalse(controller.pressedKeys.contains(KeyCode.B));
    }

    @Test
    void testKillPlayerSetsStateToDeadAndRemovesFromGrid() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        StackPane cell = new StackPane();
        GameMapController.PlayerContext ctx = new GameMapController.PlayerContext(player, cell, null);
        controller.players.add(ctx);
        controller.mapGrid.getChildren().add(cell);

        controller.killPlayer(player);

        assertEquals(Player.State.DEAD, player.getState());
        assertFalse(controller.mapGrid.getChildren().contains(cell));
    }

    @Test
    void testSpawnPowerUpAtAddsPowerUpToGridAndLists() {
        controller.gameMap = mock(GameMap.class);
        when(controller.gameMap.getTileSize()).thenReturn(32);

        int initialPowerUps = controller.mapGrid.getChildren().size();
        controller.spawnPowerUpAt(2, 3);

        assertTrue(controller.mapGrid.getChildren().size() > initialPowerUps);
        assertFalse(controller.activePowerUps.isEmpty());
        assertFalse(controller.activePowerUpCells.isEmpty());
    }
}