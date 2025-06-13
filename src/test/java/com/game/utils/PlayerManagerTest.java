package com.game.utils;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerManagerTest {

    private static final String TEST_PROFILE_DIR = "profile";
    private static final String TEST_PROFILE_FILE = "profile/players.dat";

    @BeforeEach
    void setup() {
        // Clear any existing players before each test
        PlayerManager.reloadPlayers();
        List<PlayerManager.Player> players = PlayerManager.getAllPlayers();
        for (PlayerManager.Player p : players) {
            PlayerManager.deletePlayer(p);
        }
    }

    @AfterEach
    void cleanup() {
        File file = new File(TEST_PROFILE_FILE);
        if (file.exists()) {
            assertTrue(file.delete(), "Failed to delete test profile file");
        }
    }

    @Test
    void testCreateAndGetPlayer() {
        PlayerManager.Player p = PlayerManager.createPlayer("Doe", "John", "avatar1");
        assertNotNull(p);
        assertEquals("Doe", p.getNom());
        assertEquals("John", p.getPrenom());
        assertEquals("avatar1", p.getAvatar());
    }

    @Test
    void testGetAllPlayersAfterCreation() {
        PlayerManager.createPlayer("Doe", "Jane", "avatar2");
        List<PlayerManager.Player> players = PlayerManager.getAllPlayers();
        assertEquals(1, players.size());
    }

    @Test
    void testFindPlayer() {
        PlayerManager.createPlayer("Smith", "Alice", "avatar3");
        PlayerManager.Player found = PlayerManager.findPlayer("Smith", "Alice");
        assertNotNull(found);
        assertEquals("Smith", found.getNom());
    }

    @Test
    void testDeletePlayer() {
        PlayerManager.Player p = PlayerManager.createPlayer("Test", "Delete", "avatarX");
        assertTrue(PlayerManager.deletePlayer(p));
        assertNull(PlayerManager.findPlayer("Test", "Delete"));
    }

    @Test
    void testRecordGamePlayedAndWon() {
        PlayerManager.Player p = PlayerManager.createPlayer("Champ", "Vic", "avatarY");
        PlayerManager.setCurrentPlayer(p);
        PlayerManager.recordGamePlayed();
        PlayerManager.recordGameWon();

        assertEquals(1, p.getPartiesJouees());
        assertEquals(1, p.getPartiesGagnees());
        assertEquals(100.0, p.getRatioVictoires());
    }

    @Test
    void testSetAndGetCurrentPlayer() {
        PlayerManager.Player p = PlayerManager.createPlayer("User", "Active", "avatarZ");
        PlayerManager.setCurrentPlayer(p);
        assertEquals(p, PlayerManager.getCurrentPlayer());
        assertTrue(PlayerManager.hasCurrentPlayer());
        assertEquals("Active User", PlayerManager.getCurrentPlayerName());
    }

    @Test
    void testNoCurrentPlayerDefaults() {
        PlayerManager.setCurrentPlayer(null);
        assertFalse(PlayerManager.hasCurrentPlayer());
        assertEquals("Aucun profil", PlayerManager.getCurrentPlayerName());
    }

    @Test
    void testSaveAndReloadPlayers() {
        PlayerManager.createPlayer("Persistent", "Player", "avatarP");
        PlayerManager.savePlayers();
        PlayerManager.reloadPlayers();
        assertEquals(1, PlayerManager.getPlayerCount());
    }
}
