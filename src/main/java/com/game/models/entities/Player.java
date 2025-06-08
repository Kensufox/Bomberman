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
        PauseTransition cooldown = new PauseTransition(Duration.seconds(bomb.getCOOLDOWN_SECONDS() / GameData.gameSpeed));
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

    public void setPower(PowerUp.Power power, long now, long duration) {
        if (this.power != power){
            this.power = power;
            appliPower();
        }
        this.powerEndTime = now + duration;
    }

    public PowerUp.Power getPower() {
        return power;
    }

    public void removePower() {
        this.power = null;
        setMoveDelay(originalMoveDelay); // réinitialise par défaut
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

    public void appliPower() {
        switch (power) {
            case SPEED -> setMoveDelay(moveDelay/3);
            case BOMB_RANGE -> {
            }
            case EXTRA_BOMB -> {
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
}
