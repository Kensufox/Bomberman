package com.game.models.entities.bot;

/**
 * Represents a bomb that has been placed on the game map, including its position,
 * explosion time, and blast range.
 * @version 1.0
 * @since 2025-06-09
 */
public class PlacedBomb {

    /** The row position of the bomb on the map. */
    private final int row;

    /** The column position of the bomb on the map. */
    private final int col;

    /** The timestamp (in milliseconds) when the bomb is expected to explode. */
    private final long explosionTime;

    /** The blast radius of the bomb (how many tiles it affects in each direction). */
    private final int range;

    /**
     * Constructs a new PlacedBomb with specified position, explosion time, and range.
     *
     * @param row the row index where the bomb is placed
     * @param col the column index where the bomb is placed
     * @param explosionTime the time (in milliseconds) when the bomb will explode
     * @param range the blast radius of the bomb
     */
    public PlacedBomb(int row, int col, long explosionTime, int range) {
        this.row = row;
        this.col = col;
        this.explosionTime = explosionTime;
        this.range = range;
    }

    /**
     * Gets the row position of the bomb.
     *
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column position of the bomb.
     *
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the blast radius of the bomb.
     *
     * @return the range of the bomb
     */
    public int getRange() {
        return range;
    }

    /**
     * Returns the remaining time before the bomb explodes.
     *
     * @return time left in milliseconds; returns 0 if the bomb has already exploded
     */
    public long getTimeBeforeExplosion() {
        long timeLeft = explosionTime - System.currentTimeMillis();
        return timeLeft > 0 ? timeLeft : 0;
    }

    /**
     * Checks if the bomb has already exploded.
     *
     * @return true if the current time is past the explosion time, false otherwise
     */
    public boolean hasExploded() {
        return System.currentTimeMillis() >= explosionTime;
    }
}
