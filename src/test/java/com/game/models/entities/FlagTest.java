package com.game.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlagTest {

    private Flag flag;

    @BeforeEach
    void setUp() {
        flag = new Flag(3, 4, 1);  // home position = (3, 4), teamId = 1
    }

    @Test
    void testInitialState() {
        assertEquals(3, flag.getRow());
        assertEquals(4, flag.getCol());
        assertTrue(flag.isAtHome());
        assertFalse(flag.isCarried());
        assertNull(flag.getCarrier());
        assertEquals(1, flag.getTeamId());
    }

    @Test
    void testPickupByPlayer() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        flag.pickUp(player);
        assertTrue(flag.isCarried());
        assertEquals(player, flag.getCarrier());
        assertFalse(flag.isAtHome());
    }

    @Test
    void testDropFlag() {
        Player player = new Player(1, 1, Player.State.ALIVE);
        flag.pickUp(player);
        flag.drop(2, 2);
        assertFalse(flag.isCarried());
        assertNull(flag.getCarrier());
        assertEquals(2, flag.getRow());
        assertEquals(2, flag.getCol());
    }

    @Test
    void testReturnToHome() {
        flag.drop(5, 5);
        flag.returnHome();
        assertTrue(flag.isAtHome());
        assertFalse(flag.isCarried());
        assertEquals(3, flag.getRow());
        assertEquals(4, flag.getCol());
    }
}
