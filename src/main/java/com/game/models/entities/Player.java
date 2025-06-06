package com.game.models.entities;

public class Player {
    public enum Power {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }

    private int row;
    private int col;
    private Power power;

    private long lastMoveTime = 0;
    private long moveDelay = 150_000_000; // 150ms par défaut

    public Player(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public Power getPower() {
        return power;
    }

    public void move(int dRow, int dCol) {
        this.row += dRow;
        this.col += dCol;
    }

    public boolean canMove(long now) {
        return now - lastMoveTime >= moveDelay;
    }

    public void updateLastMoveTime(long now) {
        lastMoveTime = now;
    }

    public void setMoveDelay(long delay) {
        this.moveDelay = delay;
    }

    public long getMoveDelay() {
        return moveDelay;
    }
}
