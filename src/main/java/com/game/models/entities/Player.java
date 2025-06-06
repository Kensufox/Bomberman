package com.game.models.entities;

public class Player {
    public enum Power {
        SPEED, BOMB_RANGE, EXTRA_BOMB
    }

    private int row;
    private int col;
    private Power power;

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

    public void move(int dRow, int dCol) {
        this.row += dRow;
        this.col += dCol;
    }
}
