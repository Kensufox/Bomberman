package com.game.models.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    private PowerUp powerUp;

    @BeforeEach
    void setUp() {
        powerUp = new PowerUp(2, 3, PowerUp.Power.SPEED, 5000000000L);
    }

    @Test
    void testInitialValues() {
        assertEquals(2, powerUp.getRow());
        assertEquals(3, powerUp.getCol());
        assertEquals(PowerUp.Power.SPEED, powerUp.getPower());
        assertFalse(powerUp.isCollected());
        assertEquals(5000000000L, powerUp.getDuration());
    }

    @Test
    void testMarkAsCollected() {
        powerUp.collect();
        assertTrue(powerUp.isCollected());
    }
}
