package com.game.utils;

/**
 * Utility class that holds global game configuration data such as game speed.
 * This class is currently used to store static settings accessible across the game.
 */
public class GameData {

    /** Speed multiplier that controls the pace of the game loop or animations. */
    private static final int gameSpeed = 1;

    /**
     * Retrieves the current game speed multiplier.
     *
     * @return The game speed value (default is 1).
     */
    public static int getGameSpeed() {
        return gameSpeed;
    }
}