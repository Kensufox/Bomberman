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
    protected double bombDelay;

    public Player(int startRow, int startCol, State state) {
        this.row = startRow;
        this.col = startCol;
        this.state = state;
        this.moveDelay =  150_000_000/GameData.gameSpeed;
        this.originalMoveDelay = moveDelay;
        this.bombDelay = Bomb.getCOOLDOWN_SECONDS();
    }
    /**
     * Méthode pour poser la bombe si le joueur est autorisé.
     * Elle met à jour l'heure de la dernière bombe posée et lance le cooldown.
     *
     * @param row La ligne de la position où poser la bombe.
     * @param col La colonne de la position où poser la bombe.
     * @param bomb L'Objet Bombe qui va placer la bombe
     */
    public void tryPlaceBomb(int row, int col, Bomb bomb) {
        if (!canPlaceBomb) {
            return; // Si le joueur ne peut pas poser de bombe, on sort de la méthode
        }

        canPlaceBomb = false;

        // Gérer le cooldown (on utilise un timer ici pour éviter de poser une autre bombe trop rapidement)
        PauseTransition cooldown = new PauseTransition(Duration.seconds(bombDelay / GameData.gameSpeed / placementSpeed));
        cooldown.setOnFinished(e -> canPlaceBomb = true);
        cooldown.play();

        // Appeler la méthode place() de Bombe pour poser la bombe
        bomb.place(row, col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPower(PowerUp.Power power, long now, long duration, Bomb bomb) {
        if (this.power != power){
            this.power = power;
            appliPower(bomb);
        }
        this.powerEndTime = now + duration;
    }

    public PowerUp.Power getPower() {
        return power;
    }

    public void removePower(Bomb bomb) {
        this.power = null;
        setMoveDelay(originalMoveDelay); // réinitialise par défaut
        bomb.setRange(Bomb.getOriginalRange());
        setPlacementSpeed(OriginalPlacementSpeed);
    
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
        this.moveDelay = delay/GameData.gameSpeed;
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

    public void setPlacementSpeed(float placementSpeed) {
        this.placementSpeed = placementSpeed;
    }

    public float getPlacementSpeed() {
        return placementSpeed;
    }

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

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setBombDelay(double delay) {
        this.bombDelay = delay;
    }

    public double getBombDelay() {
        return bombDelay;
    }
}
