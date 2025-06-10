package com.game.models.entities;

public class PowerUp {
    public enum Power {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }
 
    private final Power power;
    private final int row;
    private final int col;
    private boolean isCollected;
    private final long duration;

    public PowerUp(int startRow, int startCol, Power power, long duration) {
        this.row = startRow;
        this.col = startCol;
        this.power = power;
        this.duration = duration;
        this.isCollected = false;
    }

    /** 
     * @return long
     */
    public long getDuration() {
        return duration;
    }

    /** 
     * @return Power
     */
    public Power getPower() {
        return power;
    }

    /** 
     * @return int
     */
    public int getRow() {
        return row;
    }

    /** 
     * @return int
     */
    public int getCol() {
        return col;
    }

    /** 
     * @return boolean
     */
    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        isCollected = true;
    }
}
