package com.game.models.entities;

public class PowerUp {
    public enum Type {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }

    private Type type;
    private int row;
    private int col;
    private boolean isCollected;

    public PowerUp(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
        //this.type = type;
        this.isCollected = false;
    }

    public Type getType() {
        return type;
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
