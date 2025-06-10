package com.game.models.entities;

/**
 * Represents a power-up that a player can collect during gameplay.
 * Each power-up has a specific type (effect), position, duration, and collection state.
 */
public class PowerUp {
    /**
     * Enumeration of the possible power-up types.
     */
    public enum Power {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }
 
    private final Power power;
    private final int row;
    private final int col;
    private boolean isCollected;
    private final long duration;

    /**
     * Constructs a new PowerUp with the specified position, type, and duration.
     *
     * @param startRow  The row where the power-up appears.
     * @param startCol  The column where the power-up appears.
     * @param power     The type of power-up.
     * @param duration  The duration (in nanoseconds) that the power-up remains active once collected.
     */
    public PowerUp(int startRow, int startCol, Power power, long duration) {
        this.row = startRow;
        this.col = startCol;
        this.power = power;
        this.duration = duration;
        this.isCollected = false;
    }

    /**
     * @return The duration (in nanoseconds) of the power-up effect once collected.
     */
    public long getDuration() {
        return duration;
    }


    /**
     * @return The type of this power-up.
     */
    public Power getPower() {
        return power;
    }


    /**
     * @return The row position of the power-up.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The column position of the power-up.
     */
    public int getCol() {
        return col;
    }

    /**
     * @return {@code true} if the power-up has already been collected; {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Marks this power-up as collected.
     */
    public void collect() {
        isCollected = true;
    }
}
