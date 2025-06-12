package com.game.models.entities.bot;

import java.util.Objects;

import com.game.models.entities.Bomb;
import com.game.models.map.GameMap;

/**
 * Analyseur spécialisé dans la détection des bombes et zones dangereuses.
 * Fait partie du modèle dans l'architecture MVC.
 *
 * @author RADJOU Dinesh G2-5
 * @version 4.0
 * @since 2025-06-05
 */


public class BombAnalyzer {
    /** Portée d'explosion d'une bombe */
    private static final int BOMB_RANGE = Bomb.getOriginalRange() - 1;
    
    /** Référence vers la carte de jeu */
    private final GameMap gameMap;

    private Bomb bomb;

    /**
     * Constructeur de l'analyseur de bombes.
     * 
     * @param gameMap La carte de jeu à analyser
     * @throws NullPointerException si gameMap est null
     */
    public BombAnalyzer(GameMap gameMap) {
        this.gameMap = Objects.requireNonNull(gameMap, "GameMap ne peut pas être null");
    }

    /**
     * Vérifie si une position est dangereuse (dans la portée d'une bombe).
     * 
     * @param row Ligne à vérifier
     * @param col Colonne à vérifier
     * @return true si la position est dangereuse
     */
    public boolean isDangerous(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        
        for (int r = 0; r < mapData.length; r++) {
            for (int c = 0; c < mapData[0].length; c++) {
                if (isOnBomb(r, c) && isInExplosionRange(row, col, r, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si une position contient une bombe.
     */
    public boolean isOnBomb(int row, int col) {
        return gameMap.getMapData()[row][col] == 'X';
    }

    /**
     * Vérifie si une position est dans la portée d'explosion d'une bombe.
     */
    private boolean isInExplosionRange(int targetRow, int targetCol, int bombRow, int bombCol) {
        if (isOnBomb(targetRow, targetCol)) return true;

        // Vérification explosion horizontale ou verticale
        if (targetRow == bombRow || targetCol == bombCol) {
            int distance = (targetRow == bombRow) ? 
                Math.abs(targetCol - bombCol) : Math.abs(targetRow - bombRow);
            return distance <= BOMB_RANGE && 
                   !hasWallBetween(bombRow, bombCol, targetRow, targetCol);
        }
        return false;
    }

    /**
     * Vérifie s'il y a un mur entre deux positions.
     * Méthode utilitaire réutilisée par d'autres classes.
     */
    public boolean hasWallBetween(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow) {
            return checkWallsInRange(fromRow, Math.min(fromCol, toCol), 
                                   Math.max(fromCol, toCol), true);
        } else if (fromCol == toCol) {
            return checkWallsInRange(fromCol, Math.min(fromRow, toRow), 
                                   Math.max(fromRow, toRow), false);
        }
        return false;
    }

    /**
     * Vérifie la présence de murs dans une direction donnée.
     */
    private boolean checkWallsInRange(int fixedCoord, int start, int end, boolean horizontal) {
        for (int i = start + 1; i < end; i++) {
            if (horizontal ? isWall(fixedCoord, i) : isWall(i, fixedCoord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une position contient un mur.
     */
    public boolean isWall(int row, int col) {
        return isValidPosition(row, col) && gameMap.getMapData()[row][col] == 'W';
    }

    /**
     * Vérifie si une position est valide sur la carte.
     */
    public boolean isValidPosition(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        return row >= 0 && col >= 0 && row < mapData.length && col < mapData[0].length;
    }

    /**
     * Vérifie si une position est traversable.
     */
    public boolean isTraversable(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        char cell = gameMap.getMapData()[row][col];
        return cell == '.';
    }

    /**
     * Retourne les données de la carte pour les autres analyseurs.
     */
    public char[][] getMapData() {
        return gameMap.getMapData();
    }

    /**
     * Retourne un score de danger pour la case (row, col).
     * 0 = safe, plus c'est élevé plus c'est dangereux.
     */
    public int getDangerScore(int row, int col) {
        int dangerScore = 0;


        // Exemple : si la case est une bombe active
        if (isOnBomb(row, col)) {
            dangerScore += 100; // très dangereux
            System.out.println(dangerScore);
        }


        // Exemple : danger dû aux bombes proches (rayon d'explosion)
        for (PlacedBomb actBomb : bomb.getActiveBombs()) {
            int dist = manhattanDistance(row, col, actBomb.getRow(), actBomb.getCol());
            if (dist <= actBomb.getRange()) {
                // danger plus fort si plus proche de la bombe
                dangerScore += Math.max(0, 50 - dist * 5);
                System.out.println(dangerScore);
                if((actBomb.getCol() == col || actBomb.getRow() == row) ){
                    dangerScore += (int) (50 + (10 - actBomb.getTimeBeforeExplosion()) * 10);
                    System.out.println(dangerScore);
                }
            }
        }

        return dangerScore;
    }

    private int manhattanDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}