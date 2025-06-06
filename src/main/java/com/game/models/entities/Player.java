package com.game.models.entities;

public class Player {

    private int row;
    private int col;
    private PowerUp.Power power;
    private long powerEndTime = 0;


    private long lastMoveTime = 0;
    private long moveDelay = 150_000_000; // 150ms

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

    public void setPower(PowerUp.Power power, long now, long duration) {
        this.power = power;
        appliPower();
        this.powerEndTime = now + duration;
    }

    public PowerUp.Power getPower() {
        return power;
    }

    public void removePower() {
        this.power = null;
        setMoveDelay(150_000_000); // réinitialise par défaut
    }

    public long getPowerEndTime() {
        return powerEndTime;
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

    public void appliPower() {
        switch (power) {
            case SPEED -> setMoveDelay(50_000_000);
            case BOMB_RANGE -> {
            }
            case EXTRA_BOMB -> {
            }
            default -> {
            }

        }
    }
}
