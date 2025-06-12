package com.game.models.entities.bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class PlacedBombTest {

    @Test
    void testConstructorAndGetters() {
        int row = 2;
        int col = 3;
        long explosionTime = System.currentTimeMillis() + 10000;
        int range = 4;

        PlacedBomb bomb = new PlacedBomb(row, col, explosionTime, range);

        assertEquals(row, bomb.getRow());
        assertEquals(col, bomb.getCol());
        assertEquals(range, bomb.getRange());
    }

    @Test
    void testGetTimeBeforeExplosion_PositiveTime() {
        long now = System.currentTimeMillis();
        long explosionTime = now + 500;
        PlacedBomb bomb = new PlacedBomb(1, 1, explosionTime, 2);

        long timeLeft = bomb.getTimeBeforeExplosion();
        assertTrue(timeLeft <= 500 && timeLeft > 0);
    }

    @Test
    void testGetTimeBeforeExplosion_AlreadyExploded() {
        long explosionTime = System.currentTimeMillis() - 1000;
        PlacedBomb bomb = new PlacedBomb(1, 1, explosionTime, 2);

        assertEquals(0, bomb.getTimeBeforeExplosion());
    }

    @Test
    void testHasExploded_FalseBeforeExplosion() throws InterruptedException {
        long explosionTime = System.currentTimeMillis() + 200;
        PlacedBomb bomb = new PlacedBomb(0, 0, explosionTime, 1);

        assertFalse(bomb.hasExploded());
    }

    @Test
    void testHasExploded_TrueAfterExplosion() throws InterruptedException {
        long explosionTime = System.currentTimeMillis() + 50;
        PlacedBomb bomb = new PlacedBomb(0, 0, explosionTime, 1);

        Thread.sleep(60);
        assertTrue(bomb.hasExploded());
    }
}