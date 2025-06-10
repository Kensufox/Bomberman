package com.game.models.entities;

import com.game.utils.GameData;

import javafx.animation.PauseTransition;
import javafx.util.Duration;



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
    private float placementSpeed = 1;
    private final float OriginalPlacementSpeed = 1;

    protected long lastMoveTime = 0;
    protected long moveDelay;
    protected long originalMoveDelay;

    protected boolean canPlaceBomb = true;

    public Player(int startRow, int startCol, State state) {
        this.row = startRow;
        this.col = startCol;
        this.state = state;
        this.moveDelay =  150_000_000/GameData.gameSpeed;
        this.originalMoveDelay = moveDelay;
    }
    /**
     * Méthode pour poser la bombe si le joueur est autorisé.
     * Elle met à jour l'heure de la dernière bombe posée et lance le cooldown.
     *
     * @param row La ligne de la position où poser la bombe.
     * @param col La colonne de la position où poser la bombe.
     * @param currentTime Temps actuel en nanosecondes.
     */
    public void tryPlaceBomb(int row, int col, Bomb bomb) {
        if (!canPlaceBomb) {
            return; // Si le joueur ne peut pas poser de bombe, on sort de la méthode
        }

        canPlaceBomb = false;

        // Gérer le cooldown (on utilise un timer ici pour éviter de poser une autre bombe trop rapidement)
        PauseTransition cooldown = new PauseTransition(Duration.seconds(Bomb.getCOOLDOWN_SECONDS() / GameData.gameSpeed / placementSpeed));
        cooldown.setOnFinished(e -> canPlaceBomb = true);
        cooldown.play();

        // Appeler la méthode place() de Bombe pour poser la bombe
        bomb.place(row, col);
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
     * @param power
     * @param now
     * @param duration
     * @param bomb
     */
    public void setPower(PowerUp.Power power, long now, long duration, Bomb bomb) {
        if (this.power != power){
            this.power = power;
            appliPower(bomb);
        }
        this.powerEndTime = now + duration;
    }

    /** 
     * @return Power
     */
    public PowerUp.Power getPower() {
        return power;
    }

    /** 
     * @param bomb
     */
    public void removePower(Bomb bomb) {
        this.power = null;
        setMoveDelay(originalMoveDelay); // réinitialise par défaut
        bomb.setRange(Bomb.getOriginalRange());
        setPlacementSpeed(OriginalPlacementSpeed);
    
    }

    /** 
     * @return long
     */
    public long getPowerEndTime() {
        return powerEndTime;
    }


    /** 
     * @param dRow
     * @param dCol
     */
    public void move(int dRow, int dCol) {
        this.row += dRow;
        this.col += dCol;
    }

    /** 
     * @param now
     * @return boolean
     */
    public boolean canMove(long now) {
        return now - lastMoveTime >= moveDelay;
    }

    /** 
     * @param now
     */
    public void updateLastMoveTime(long now) {
        lastMoveTime = now;
    }

    /** 
     * @param delay
     */
    public void setMoveDelay(long delay) {
        this.moveDelay = delay/GameData.gameSpeed;
    }

    /** 
     * @return long
     */
    public long getMoveDelay() {
        return moveDelay;
    }

    /** 
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

    /** 
     * @return State
     */
    public State getState() {
        return state;
    }

    /** 
     * @param placementSpeed
     */
    public void setPlacementSpeed(float placementSpeed) {
        this.placementSpeed = placementSpeed;
    }

    /** 
     * @return float
     */
    public float getPlacementSpeed() {
        return placementSpeed;
    }

    /** 
     * @param bomb
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
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /** 
     * @return int
     */
    public int getScore() {
        return score;
    }
}
