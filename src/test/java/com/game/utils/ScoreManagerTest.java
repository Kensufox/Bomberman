package com.game.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScoreManagerTest {
    @BeforeEach
    void setUp() {
        ScoreManager.setP1Score(1);
        ScoreManager.setP2Score(2);
    }

    @Test
    void testGetP1Score() {
        assertEquals(1, ScoreManager.getP1Score());
    }

    @Test
    void testGetP2Score() {
        assertEquals(2, ScoreManager.getP2Score());
    }

    @Test
    void testSetP1Score() {
        ScoreManager.setP1Score(0);
        assertEquals(0, ScoreManager.getP1Score());
    }

    @Test
    void testSetP2Score() {
        ScoreManager.setP2Score(4);
        assertEquals(4, ScoreManager.getP2Score());
    }

    @Test
    void testIncrementP1Score() {
        ScoreManager.incrementP1Score();
        assertEquals(2, ScoreManager.getP1Score());
    }

    @Test
    void testIncrementP2Score() {
        ScoreManager.incrementP2Score();
        assertEquals(3, ScoreManager.getP2Score());
    }
    
    @Test
    void testResetScore() {
        ScoreManager.resetScores();
        assertEquals(0, ScoreManager.getP1Score());
        assertEquals(0, ScoreManager.getP2Score());
    }
}
