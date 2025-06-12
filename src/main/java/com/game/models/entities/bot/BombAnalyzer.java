package com.game.models.entities.bot;

import com.game.models.entities.Bomb;
import com.game.models.map.GameMap;

import java.util.Objects;

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
    private static final int BOMB_RANGE = Bomb.getOriginalRange() + 1;

    /** Référence vers la carte de jeu */
    private final GameMap gameMap;

    private Bomb bomb;

    /**
     * Constructeur de l'analyseur de bombes.
     * @param gameMap La carte du jeu à analyser
     * @throws NullPointerException si gameMap est null
     */
    public BombAnalyzer(GameMap gameMap) {
        this.gameMap = Objects.requireNonNull(gameMap, "GameMap ne peut pas être null");
    }

    /**
     * Vérifie si une position est dangereuse (dans le rayon d'explosion d'une bombe).
     * Attention, le bot prends une case en plus comme dangereux
     * @param row Ligne à vérifier
     * @param col Colonne à vérifier
     * @return true si la position est dangereuse
     */
    public boolean isDangerous(int row, int col) {
        if (!isValidPosition(row, col) || isWall(row, col)) return false;
        if (bomb == null || bomb.getActiveBombs() == null) return false;
        for (PlacedBomb actBomb : bomb.getActiveBombs()) {
            int bombRow = actBomb.getRow();
            int bombCol = actBomb.getCol();
            if (isInExplosionRange(row, col, bombRow, bombCol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si la position spécifiée contient une bombe active.
     *
     * @param row L'indice de la ligne sur la carte.
     * @param col L'indice de la colonne sur la carte.
     * @return true si une bombe est présente à cette position, false sinon.
     */
    public boolean isOnBomb(int row, int col) {
        return gameMap.getMapData()[row][col] == 'X';
    }

    /**
     * Vérifie si une position donnée est dans la portée d'explosion d'une bombe.
     *
     * @param targetRow La ligne de la position cible.
     * @param targetCol La colonne de la position cible.
     * @param bombRow   La ligne où la bombe est placée.
     * @param bombCol   La colonne où la bombe est placée.
     * @return true si la position cible est dans la portée d'explosion, false sinon.
     */
    public boolean isInExplosionRange(int targetRow, int targetCol, int bombRow, int bombCol) {
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
     * Vérifie s'il y a un mur entre deux positions sur la carte.
     * Utile pour déterminer si une explosion est bloquée.
     *
     * @param fromRow Ligne de départ.
     * @param fromCol Colonne de départ.
     * @param toRow   Ligne d'arrivée.
     * @param toCol   Colonne d'arrivée.
     * @return true s'il y a un mur entre les deux positions, false sinon.
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
     * Parcourt une ligne ou une colonne entre deux indices pour vérifier la présence de murs.
     *
     * @param fixedCoord Coordonnée fixe (ligne ou colonne selon l'axe).
     * @param start      Début de la plage à vérifier (exclu).
     * @param end        Fin de la plage à vérifier (exclu).
     * @param horizontal true si l'axe est horizontal (ligne fixe), false sinon.
     * @return true si un mur est trouvé dans la plage, false sinon.
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
     * Indique si une position donnée contient un mur.
     *
     * @param row Ligne à vérifier.
     * @param col Colonne à vérifier.
     * @return true si la position contient un mur, false sinon.
     */
    public boolean isWall(int row, int col) {
        return isValidPosition(row, col) && gameMap.getMapData()[row][col] == 'W';
    }

    /**
     * Vérifie si une position est valide dans les limites de la carte.
     *
     * @param row Ligne à vérifier.
     * @param col Colonne à vérifier.
     * @return true si la position est valide, false sinon.
     */
    public boolean isValidPosition(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        return row >= 0 && col >= 0 && row < mapData.length && col < mapData[0].length;
    }

    /**
     * Vérifie si une position est traversable (pas d'obstacle).
     *
     * @param row Ligne à vérifier.
     * @param col Colonne à vérifier.
     * @return true si la position est traversable, false sinon.
     */
    public boolean isTraversable(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        char cell = gameMap.getMapData()[row][col];
        return cell == '.';
    }

    /**
     * Retourne une copie des données de la carte.
     *
     * @return Un tableau 2D de char représentant la carte.
     */
    public char[][] getMapData() {
        return gameMap.getMapData();
    }

    /**
     * Calcule un score de danger pour une position donnée sur la carte.
     * Plus le score est élevé, plus la position est dangereuse.
     *
     * @param row Ligne de la position.
     * @param col Colonne de la position.
     * @return Un entier représentant le niveau de danger.
     */
    public int getDangerScore(int row, int col) {
        int dangerScore = 0;


        // Exemple : si la case est une bombe active
        if (isOnBomb(row, col)) {
            dangerScore += 100; // très dangereux
        }


        // Exemple : danger dû aux bombes proches (rayon d'explosion)
        for (PlacedBomb actBomb : bomb.getActiveBombs()) {
            int dist = manhattanDistance(row, col, actBomb.getRow(), actBomb.getCol());
            if (dist <= actBomb.getRange()) {
                // danger plus fort si plus proche de la bombe
                dangerScore += Math.max(0, 50 - dist * 5);
                if((actBomb.getCol() == col || actBomb.getRow() == row) ){
                    dangerScore += (int) (50 + (10 - actBomb.getTimeBeforeExplosion()) * 10);
                }
            }
        }

        return dangerScore;
    }

    /**
     * Calcule la distance de Manhattan entre deux points dans une grille.
     *
     * @param r1 l'indice de ligne du premier point
     * @param c1 l'indice de colonne du premier point
     * @param r2 l'indice de ligne du second point
     * @param c2 l'indice de colonne du second point
     * @return la distance de Manhattan (|r1 - r2| + |c1 - c2|)
     */
    private int manhattanDistance(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    /**
     * Définit la bombe actuelle associée à cet objet.
     *
     * @param bomb l'objet Bomb à assigner
     */
    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}