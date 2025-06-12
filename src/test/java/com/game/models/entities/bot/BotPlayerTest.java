package com.game.models.entities.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.game.models.entities.Player;
import com.game.models.map.GameMap;

public class BotPlayerTest {

    private GameMap gameMap;
    private BotPlayer bot;
    private Player enemy;

    @BeforeEach
    public void setUp() {
        char[][] mapData = {
            {' ', ' ', ' ', ' ', ' '},
            {' ', '#', '#', '#', ' '},
            {' ', '#', ' ', '#', ' '},
            {' ', '#', '#', '#', ' '},
            {' ', ' ', ' ', ' ', ' '}
        };
        gameMap = new GameMap();
        bot = new BotPlayer(2, 2, Player.State.ALIVE, gameMap);
        enemy = new Player(0, 0, Player.State.ALIVE);
    }

    @Test
    public void testBotInitialization() {
        assertEquals(2, bot.getRow());
        assertEquals(2, bot.getCol());
        assertEquals(Player.State.ALIVE, bot.getState());
        assertNotNull(bot.getBombAnalyzer());
        assertNotNull(bot.getMovementStrategy());
    }

    @Test
    public void testDecideMoveSafePosition() {
        int[] move = bot.decideMove(System.nanoTime(), enemy);
        assertNotNull(move);
        assertEquals(2, move.length);
    }

    @Test
    public void testDecideActionFormat() {
        int[] action = bot.decideAction(System.nanoTime(), enemy);
        assertEquals(3, action.length);
        assertTrue(action[2] == 0 || action[2] == 1, "ShouldPlaceBomb must be 0 or 1");
    }

    @Test
    public void testShouldPlaceBombFalseWhenCooldownNotElapsed() {
        bot.setLastBombTime(System.nanoTime()); // Just set it now
        boolean result = bot.shouldPlaceBomb(System.nanoTime(), enemy);
        assertFalse(result);
    }

    @Test
    public void testSetAndGetLastBombTime() {
        long now = System.nanoTime();
        bot.setLastBombTime(now);
        assertEquals(now, bot.getLastBombTime());
    }

    @Test
    public void testEnemyInBombRangeDetection() {
        enemy = new Player(2, 4, Player.State.ALIVE); // In horizontal range
        bot.decideAction(System.nanoTime(), enemy);
        assertTrue(bot.getDebugInfo().contains("Enemy in Range"));
    }

    @Test
    public void testCanEscapeAfterBomb() {
        enemy = new Player(0, 0, Player.State.ALIVE);
        bot.setLastBombTime(System.nanoTime() - 2_000_000_000); // Set 2s ago
        int[] action = bot.decideAction(System.nanoTime(), enemy);
        if (action[2] == 1) {
            assertTrue(bot.getDebugInfo().contains("Can Escape After: ✅ YES"));
        }
    }

    @Test
    public void testDangerZoneAndEscape() {
        bot.getBombAnalyzer().getMapData()[2][2] = 'X'; // Mark bot as on bomb
        assertTrue(bot.getDebugInfo().contains("🔥 STANDING ON BOMB"));
    }

    @Test
    public void testDebugInfoContainsStatus() {
        String debug = bot.getDebugInfo();
        assertTrue(debug.contains("BOT STATUS"));
        assertTrue(debug.contains("SAFETY ANALYSIS"));
        assertTrue(debug.contains("STRATEGY & PATHFINDING"));
    }

    @Test
    public void testDetermineStrategyEscapeMode() {
        bot.getBombAnalyzer().getMapData()[2][2] = 'X';
        String debug = bot.getDebugInfo();
        assertTrue(debug.contains("🏃 ESCAPE MODE"));
        assertTrue(debug.contains("🎯 ATTACK MODE"));
        assertTrue(debug.contains("🕵️  HUNT MODE"));
        assertTrue(debug.contains("⏳ WAIT MODE"));
    }
}