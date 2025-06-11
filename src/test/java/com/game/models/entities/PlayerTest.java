package com.game.models.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(1, 1, Player.State.ALIVE);
    }

    @Test
    void testInitialState() {
        assertEquals(1, player.getRow());
        assertEquals(1, player.getCol());
        assertEquals(Player.State.ALIVE, player.getState());
        assertEquals(0, player.getScore());
    }

    @Test
    void testSetAndGetScore() {
        player.setScore(100);
        assertEquals(100, player.getScore());
    }

    @Test
    void testPowerUpActivation() {
        player.setPower(PowerUp.Power.SPEED, 0, 0, null);
        assertEquals(PowerUp.Power.SPEED, player.getPower());
    }

    @Test
    void testPlayerCanPlaceBomb() {
        assertTrue(player.canPlaceBomb);
        player.setCanPlaceBomb(false);
        assertFalse(player.canPlaceBomb);
    }
}
