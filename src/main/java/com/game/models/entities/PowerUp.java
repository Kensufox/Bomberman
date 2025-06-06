package com.game.models.entities;

public class PowerUp {
    public enum Power {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }
 
    private Power power;
    private int row;
    private int col;
    private boolean isCollected;
    private long duration;

    public PowerUp(int startRow, int startCol, Power power, long duration) {
        this.row = startRow;
        this.col = startCol;
        this.power = power;
        this.duration = duration;
        this.isCollected = false;
    }

    public long getDuration() {
        return duration;
    }

    public Power getPower() {
        return power;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void collect() {
        isCollected = true;
    }
}
