package com.game.models.entities;

import com.game.utils.GameData;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Represents a player in the game, with position, power-ups, state, movement timing,
 * and the ability to place bombs.
 */
public class Player {
    /**
     * Enum representing the possible states of a player.
     */
    public enum State {
        ALIVE, DEAD, GHOST
    }

    /** The current row position of the player. */
    protected int row;

    /** The current column position of the player. */
    protected int col;

    /** The power-up currently held by the player. */
    protected PowerUp.Power power;

    /** The current state of the player. */
    protected State state;

    /** Timestamp when the player's current power-up effect ends. */
    protected long powerEndTime = 0;

    /** The player's current score. */
    protected int score = 0;

    /** The speed at which the player can place bombs. */
    private float placementSpeed = 1;

    /** The original (default) bomb placement speed for the player. */
    private final float OriginalPlacementSpeed = 1;

    /** Timestamp of the last movement performed by the player. */
    protected long lastMoveTime = 0;

    /** Delay between player movements in milliseconds. */
    protected long moveDelay;

    /** The original (default) movement delay for the player. */
    protected long originalMoveDelay;

    /** Whether the player is currently allowed to place a bomb. */
    protected boolean canPlaceBomb = true;

    /** Delay between bomb placements for the player. */
    protected double bombDelay;

    /** Indicates whether the player is connected with a profile. */
    private boolean PlayerConnected = false;


    /**
     * Constructs a player with a starting position and initial state.
     *
     * @param startRow Starting row on the game grid.
     * @param startCol Starting column on the game grid.
     * @param state    Initial state of the player (ALIVE, DEAD, or GHOST).
     */
    public Player(int startRow, int startCol, State state) {
        this.row = startRow;
        this.col = startCol;
        this.state = state;
        this.moveDelay =  150_000_000/GameData.getGameSpeed();
        this.originalMoveDelay = moveDelay;
        this.bombDelay = Bomb.getCOOLDOWN_SECONDS();
    }

    /**
     * Attempts to place a bomb if the cooldown allows.
     *
     * @param row         The row at which to place the bomb.
     * @param col         The column at which to place the bomb.
     * @param bomb        The bomb to be placed.
     */
    public void tryPlaceBomb(int row, int col, Bomb bomb) {
        if (!canPlaceBomb) {
            return; // Si le joueur ne peut pas poser de bombe, on sort de la méthode
        }

        canPlaceBomb = false;

        // Gérer le cooldown (on utilise un timer ici pour éviter de poser une autre bombe trop rapidement)
        PauseTransition cooldown = new PauseTransition(Duration.seconds(bombDelay / GameData.getGameSpeed() / placementSpeed));
        cooldown.setOnFinished(e -> canPlaceBomb = true);
        cooldown.play();

        // Appeler la méthode place() de Bombe pour poser la bombe
        bomb.place(row, col);
    }

    /**
     * @return The current row of the player.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The current column of the player.
     */
    public int getCol() {
        return col;
    }

    /**
     * Assigns a power-up to the player for a certain duration.
     *
     * @param power    The power-up to apply.
     * @param now      Current time in nanoseconds.
     * @param duration Duration of the power-up effect.
     * @param bomb     The bomb object affected by some power-ups.
     */
    public void setPower(PowerUp.Power power, long now, long duration, Bomb bomb) {
        if (this.power != power){
            this.power = power;
            appliPower(bomb);
        }
        this.powerEndTime = now + duration;
    }

    /**
     * @return The player's current power-up.
     */
    public PowerUp.Power getPower() {
        return power;
    }

    /**
     * Removes the player's current power-up and resets all related attributes.
     *
     * @param bomb The bomb object to reset range if applicable.
     */
    public void removePower(Bomb bomb) {
        this.power = null;
        setMoveDelay(originalMoveDelay); // réinitialise par défaut
        bomb.setRange(Bomb.getOriginalRange());
        setPlacementSpeed(OriginalPlacementSpeed);
    
    }

    /**
     * @return The time (in nanoseconds) at which the current power expires.
     */
    public long getPowerEndTime() {
        return powerEndTime;
    }

    /**
     * Moves the player by the specified delta.
     *
     * @param dRow Change in row.
     * @param dCol Change in column.
     */
    public void move(int dRow, int dCol) {
        this.row += dRow;
        this.col += dCol;
    }


    /**
     * Checks if the player can move based on the current time and movement delay.
     *
     * @param now Current time in nanoseconds.
     * @return true if the player can move, false otherwise.
     */
    public boolean canMove(long now) {
        return now - lastMoveTime >= moveDelay;
    }

    /**
     * Updates the time when the player last moved.
     *
     * @param now Current time in nanoseconds.
     */
    public void updateLastMoveTime(long now) {
        lastMoveTime = now;
    }

    /**
     * Sets the player's movement delay based on the game speed.
     *
     * @param delay The delay between moves in nanoseconds.
     */
    public void setMoveDelay(long delay) {
        this.moveDelay = delay/GameData.getGameSpeed();
    }


    /**
     * @return The current delay between player movements.
     */
    public long getMoveDelay() {
        return moveDelay;
    }

    /**
     * Sets the player's state (e.g. ALIVE, DEAD, GHOST).
     *
     * @param state The new state to assign.
     */
    public void setState(State state) {
        this.state = state;
    }


    /**
     * @return The current state of the player.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the bomb placement speed modifier.
     *
     * @param placementSpeed The new placement speed factor.
     */
    public void setPlacementSpeed(float placementSpeed) {
        this.placementSpeed = placementSpeed;
    }

    /**
     * @return The current bomb placement speed modifier.
     */
    public float getPlacementSpeed() {
        return placementSpeed;
    }

    /**
     * Applies the player's current power-up effects.
     *
     * @param bomb The bomb object affected by power-ups.
     */
    public void appliPower(Bomb bomb) {
        switch (power) {
            case SPEED -> setMoveDelay(moveDelay/3);
            case BOMB_RANGE -> {
                bomb.setRange(bomb.getRange()+1);
            }
            case EXTRA_BOMB -> {
                setPlacementSpeed((float) 0.5);
            }
            default -> {
            }

        }
    }

    /**
     * Sets the player's score.
     *
     * @param score The new score value.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return The current score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the delay between bomb placements.
     *
     * @param delay The delay in seconds (or game time units) before the player can place another bomb.
     */
    public void setBombDelay(double delay) {
        this.bombDelay = delay;
    }

    /**
     * Gets the current delay between bomb placements.
     *
     * @return The bomb placement delay.
     */
    public double getBombDelay() {
        return bombDelay;
    }

    /**
     * Returns whether the player is currently connected.
     *
     * @return true if the player is connected; false otherwise.
     */
    public boolean getPlayerConnected() {
        return PlayerConnected;
    }

    /**
     * Sets the player's connection status.
     *
     * @param playerConnected true if the player is connected; false otherwise.
     */
    public void setPlayerConnected(boolean playerConnected) {
        PlayerConnected = playerConnected;
    }

    /**
     * Sets whether the player can place bombs.
     *
     * @param canPlaceBomb true if the player is allowed to place bombs; false otherwise.
     */
    public void setCanPlaceBomb(boolean canPlaceBomb) {
        this.canPlaceBomb = canPlaceBomb;
    }
}
