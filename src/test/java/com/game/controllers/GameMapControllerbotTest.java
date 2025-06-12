package com.game.controllers;

import com.game.models.entities.Player;
import com.game.models.entities.bot.BotPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;



/**
 * Unit tests for GameMapControllerbot.
 */
class GameMapControllerbotTest {

    private GameMapControllerbot controller;

    @BeforeEach
    void setUp() {
        controller = new GameMapControllerbot();
        // Mock the gameMap dependency if needed
        controller.gameMap = mock(com.game.models.map.GameMap.class);
    }

    @Test
    void testDefaultBotDifficultyIsMedium() {
        assertEquals("MEDIUM", controller.getBotDifficulty());
    }

    @Test
    void testSetAndGetBotDifficulty() {
        controller.setBotDifficulty("EASY");
        assertEquals("EASY", controller.getBotDifficulty());

        controller.setBotDifficulty("HARD");
        assertEquals("HARD", controller.getBotDifficulty());
    }

    @Test
    void testCreatePlayersReturnsHumanAndBot() {
        Player[] players = controller.createPlayers();
        assertNotNull(players);
        assertEquals(2, players.length);
        assertTrue(players[0] instanceof Player);
        assertTrue(players[1] instanceof BotPlayer);
    }

    @Test
    void testConfigureBotDifficultyEasy() {
        controller.setBotDifficulty("EASY");
        Player[] players = controller.createPlayers();
        BotPlayer bot = (BotPlayer) players[1];

        assertEquals(500_000_000L, bot.getMoveDelay());
        assertEquals(3, bot.getBombDelay());
        assertEquals(1, bot.getIntelligenceLevel());
    }

    @Test
    void testConfigureBotDifficultyMedium() {
        controller.setBotDifficulty("MEDIUM");
        Player[] players = controller.createPlayers();
        BotPlayer bot = (BotPlayer) players[1];

        assertEquals(300_000_000L, bot.getMoveDelay());
        assertEquals(2, bot.getBombDelay());
        assertEquals(2, bot.getIntelligenceLevel());
    }

    @Test
    void testConfigureBotDifficultyHard() {
        controller.setBotDifficulty("HARD");
        Player[] players = controller.createPlayers();
        BotPlayer bot = (BotPlayer) players[1];

        assertEquals(150_000_000L, bot.getMoveDelay());
        assertEquals(1, bot.getBombDelay());
        assertEquals(3, bot.getIntelligenceLevel());
    }

    @Test
    void testConfigureBotDifficultyDefault() {
        controller.setBotDifficulty("UNKNOWN");
        Player[] players = controller.createPlayers();
        BotPlayer bot = (BotPlayer) players[1];

        assertEquals(300_000_000L, bot.getMoveDelay());
        assertEquals(2, bot.getBombDelay());
        assertEquals(2, bot.getIntelligenceLevel());
    }
}