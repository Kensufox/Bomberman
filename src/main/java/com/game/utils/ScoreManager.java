package com.game.utils;

public class ScoreManager {
    private static int P1Score = 0;
    private static int P2Score = 0;

    /** 
     * @return int
     */
    public static int getP1Score() {
        return P1Score;
    }

    /** 
     * @return int
     */
    public static int getP2Score() {
        return P2Score;
    }

    /** 
     * @param score
     */
    public static void setP1Score(int score) {
        P1Score = score;
    }

    /** 
     * @param score
     */
    public static void setP2Score(int score) {
        P2Score = score;
    }

    public static void incrementP1Score() {
        P1Score++;
    }

    public static void incrementP2Score() {
        P2Score++;
    }

    public static void resetScores() {
        P1Score = 0;
        P2Score = 0;
    }
}
