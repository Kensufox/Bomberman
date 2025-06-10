package com.game.models.entities.bot;

public class PlacedBomb {
    private final int row;
    private final int col;
    private final long explosionTime; // timestamp en ms
    private final int range;

    public PlacedBomb(int row, int col, long explosionTime, int range) {
        this.row = row;
        this.col = col;
        this.explosionTime = explosionTime;
        this.range = range;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getRange() { return range; }

    public long getTimeBeforeExplosion() {
        long timeLeft = explosionTime - System.currentTimeMillis();
        return timeLeft > 0 ? timeLeft : 0;
    }

    public boolean hasExploded() {
        return System.currentTimeMillis() >= explosionTime;
    }
}
