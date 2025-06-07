package com.game.models.entities;

public class Player {
    public enum State {
        ALIVE, DEAD, GHOST
    }

    protected int row;
    protected int col;
    protected PowerUp.Power power;
    protected State state;
    protected long powerEndTime = 0;
    protected int score = 0;

    private boolean isBot = false;

    protected long lastMoveTime = 0;
    protected long moveDelay;

    public Player(int startRow, int startCol, State state) {
        this.row = startRow;
        this.col = startCol;
        this.state = state;
        this.moveDelay = 150_000_000; // 150ms
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

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
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

    public void setIsBot(boolean isBot) {
        this.isBot = isBot;
    }

    public boolean getIsBot() {
        return isBot;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
