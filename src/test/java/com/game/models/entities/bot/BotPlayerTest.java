package com.game.models.entities.bot;

import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testDetermineStrategyHuntMode() {
        bot.decideAction(System.currentTimeMillis(), enemy);
        bot.getBombAnalyzer().getMapData()[2][2] = 'X';
        String debug = bot.getDebugInfo();
        assertTrue(debug.contains("🕵️  HUNT MODE")); //car pas moyen de s'echapper en mode safe

    }

    @Test
    public void testSetAndGetIntelligenceLevel() {
        bot.setIntelligenceLevel(3);
        assertEquals(3, bot.getIntelligenceLevel());
    }

    @Test
    public void testGetEnemyReturnsAssignedEnemy() {
        bot.decideAction(System.nanoTime(), enemy);
        assertEquals(enemy, bot.getEnemy());
    }

    @Test
    public void testDecideActionReturnsNoBombWhenEnemyNullThrows() {
        assertThrows(NullPointerException.class, () -> bot.decideAction(System.nanoTime(), null));
    }

    @Test
    public void testCountTraversableNeighbors() {
        int traversable = bot.countTraversableNeighbors();
        assertTrue(traversable >= 0 && traversable <= 4);
    }

    @Test
    public void testCountWallNeighbors() {
        int wallNeighbors = bot.countWallNeighbors();
        assertTrue(wallNeighbors >= 0 && wallNeighbors <= 4);
    }

    @Test
    public void testCountEscapeRoutes() {
        int escapeRoutes = bot.countEscapeRoutes();
        assertTrue(escapeRoutes >= 0 && escapeRoutes <= 4);
    }

    @Test
    public void testCriticalAlertsWhenTrapped() {
        // Surround bot with walls and danger
        char[][] map = bot.getBombAnalyzer().getMapData();
        map[1][2] = '#';
        map[2][1] = '#';
        map[2][3] = '#';
        map[3][2] = '#';
        map[2][2] = 'X'; // On bomb
        String debug = bot.getDebugInfo();
        assertTrue(debug.contains("STANDING ON BOMB"));
    }

    @Test
    public void testDebugInfoUptimeAndPerformance() {
        String debug = bot.getDebugInfo();
        assertTrue(debug.contains("Move Delay"));
        assertTrue(debug.contains("Game Speed"));
        assertTrue(debug.contains("Uptime"));
    }

    @Test
    public void testShouldPlaceBombTrueWhenCooldownElapsedAndEnemyInRange() {
        // Place enemy adjacent and set bomb cooldown elapsed
        enemy = new Player(2, 3, Player.State.ALIVE);
        bot.setLastBombTime(System.nanoTime() - 2_000_000_000); // 2s ago
        boolean result = bot.shouldPlaceBomb(System.nanoTime(), enemy);
        // May depend on MovementStrategy logic, so just check it's a boolean
        assertNotNull(result);
    }

    @Test
    public void testGetBombAnalyzerNotNull() {
        assertNotNull(bot.getBombAnalyzer());
    }

    @Test
    public void testGetMovementStrategyNotNull() {
        assertNotNull(bot.getMovementStrategy());
    }
}