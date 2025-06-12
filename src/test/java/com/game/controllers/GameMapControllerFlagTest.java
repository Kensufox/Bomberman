package com.game.controllers;

import com.game.models.entities.Player;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



class GameMapControllerFlagTest {

    private GameMapControllerFlag controller;

    @BeforeEach
    void setUp() {
        controller = Mockito.spy(new GameMapControllerFlag());
        controller.mapGrid = new GridPane();
        controller.backgroundGrid = new GridPane();
        controller.gameMap = mock(com.game.models.map.GameMap.class);
        when(controller.gameMap.getMapData()).thenReturn(new char[][]{
                {'.', '.', '.'},
                {'.', '.', '.'},
                {'.', '.', '.'}
        });
        when(controller.gameMap.getTileSize()).thenReturn(32);
    }

    @Test
    void testFlagInitialization() {
        controller.players.clear();
        controller.players.add(new GameMapControllerFlag.PlayerContext(
                new Player(1, 1, Player.State.ALIVE),
                new StackPane(),
                mock(com.game.utils.InputHandler.PlayerControls.class),
                1, 1
        ));
        controller.players.add(new GameMapControllerFlag.PlayerContext(
                new Player(2, 2, Player.State.ALIVE),
                new StackPane(),
                mock(com.game.utils.InputHandler.PlayerControls.class),
                2, 2
        ));
        controller.setupFlags();
        assertNotNull(controller.getPlayer1Flag());
        assertNotNull(controller.getPlayer2Flag());
        assertEquals(1, controller.getPlayer1Flag().getRow());
        assertEquals(1, controller.getPlayer1Flag().getCol());
        assertEquals(11, controller.getPlayer2Flag().getHomeRow());
        assertEquals(13, controller.getPlayer2Flag().getHomeCol());
    }

    @Test
    void testIsWalkableReturnsTrueForDotOrP() {
        when(controller.gameMap.getMapData()).thenReturn(new char[][]{
                {'.', 'P'},
                {'#', 'X'}
        });
        assertTrue(controller.isWalkable(0, 0));
        assertTrue(controller.isWalkable(0, 1));
        assertFalse(controller.isWalkable(1, 0));
        assertFalse(controller.isWalkable(1, 1));
    }

    @Test
    void testIsWalkableOutOfBounds() {
        when(controller.gameMap.getMapData()).thenReturn(new char[][]{
                {'.'}
        });
        assertFalse(controller.isWalkable(-1, 0));
        assertFalse(controller.isWalkable(0, -1));
        assertFalse(controller.isWalkable(1, 0));
        assertFalse(controller.isWalkable(0, 1));
    }

    @Test
    void testFlagPickUpAndReturn() {
        GameMapControllerFlag.Flag flag = new GameMapControllerFlag.Flag(1, 1);
        Player player = new Player(2, 2, Player.State.ALIVE);
        assertTrue(flag.isAtHome());
        assertFalse(flag.isCarried());
        flag.pickUp(player);
        assertTrue(flag.isCarried());
        assertEquals(player, flag.getCarrier());
        assertFalse(flag.isAtHome());
        flag.returnHome();
        assertFalse(flag.isCarried());
        assertNull(flag.getCarrier());
        assertTrue(flag.isAtHome());
        assertEquals(1, flag.getRow());
        assertEquals(1, flag.getCol());
    }

    @Test
    void testFlagDrop() {
        GameMapControllerFlag.Flag flag = new GameMapControllerFlag.Flag(1, 1);
        Player player = new Player(2, 2, Player.State.ALIVE);
        flag.pickUp(player);
        flag.drop(2, 2);
        assertFalse(flag.isCarried());
        assertNull(flag.getCarrier());
        assertEquals(2, flag.getRow());
        assertEquals(2, flag.getCol());
        assertFalse(flag.isAtHome());
    }

    @Test
    void testMovePlayerIfPossibleWalkable() {
        Player player = new Player(0, 0, Player.State.ALIVE);
        StackPane cell = new StackPane();
        when(controller.gameMap.getMapData()).thenReturn(new char[][]{
                {'.', '.'},
                {'.', '.'}
        });
        controller.movePlayerIfPossible(player, cell, 1, 0);
        assertEquals(1, player.getRow());
        assertEquals(0, player.getCol());
    }

    @Test
    void testMovePlayerIfPossibleBlocked() {
        Player player = new Player(0, 0, Player.State.ALIVE);
        StackPane cell = new StackPane();
        when(controller.gameMap.getMapData()).thenReturn(new char[][]{
                {'.', '#'},
                {'#', '#'}
        });
        controller.movePlayerIfPossible(player, cell, 1, 1);
        // Should not move
        assertEquals(0, player.getRow());
        assertEquals(0, player.getCol());
    }

    @Test
    void testKillPlayerSetsStateDead() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        GameMapControllerFlag.PlayerContext ctx = new GameMapControllerFlag.PlayerContext(
                player, new StackPane(), mock(com.game.utils.InputHandler.PlayerControls.class), 1, 1
        );
        controller.players.clear();
        controller.players.add(ctx);
        controller.killPlayer(player);
        assertEquals(Player.State.DEAD, player.getState());
    }

    @Test
    void testCreatePlayers() {
        Player[] players = controller.createPlayers();
        assertEquals(2, players.length);
        assertEquals(1, players[0].getRow());
        assertEquals(1, players[0].getCol());
        assertEquals(11, players[1].getRow());
        assertEquals(13, players[1].getCol());
    }
}