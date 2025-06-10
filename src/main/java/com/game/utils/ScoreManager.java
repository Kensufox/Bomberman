package com.game.utils;

/**
 * Manages the scores for Player 1 and Player 2.
 * Provides static methods to get, set, increment, and reset scores.
 */
public class ScoreManager {
    private static int P1Score = 0;
    private static int P2Score = 0;

    /**
     * Gets the current score of Player 1.
     * 
     * @return the score of Player 1
     */
    public static int getP1Score() {
        return P1Score;
    }

    /**
     * Gets the current score of Player 2.
     * 
     * @return the score of Player 2
     */
    public static int getP2Score() {
        return P2Score;
    }

    /**
     * Sets the score of Player 1 to the specified value.
     * 
     * @param score the new score for Player 1
     */
    public static void setP1Score(int score) {
        P1Score = score;
    }

    /**
     * Sets the score of Player 2 to the specified value.
     * 
     * @param score the new score for Player 2
     */
    public static void setP2Score(int score) {
        P2Score = score;
    }

    /**
     * Increments Player 1's score by 1.
     */
    public static void incrementP1Score() {
        P1Score++;
    }

    /**
     * Increments Player 2's score by 1.
     */
    public static void incrementP2Score() {
        P2Score++;
    }

    /**
     * Resets both Player 1 and Player 2 scores to 0.
     */
    public static void resetScores() {
        P1Score = 0;
        P2Score = 0;
    }
}
