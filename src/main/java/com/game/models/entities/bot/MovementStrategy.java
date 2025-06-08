package com.game.models.entities.bot;

import com.game.models.entities.Player;
import java.util.List;

/**
 * Stratégie de mouvement coordonnant l'analyse et la recherche de chemin.
 * Implémente la logique métier du bot (Modèle dans MVC).
 *
 * @author RADJOU Dinesh G2-5
 * @version 4.0
 * @since 2025-06-05
 */


public class MovementStrategy {
    /** Directions possibles : haut, bas, gauche, droite */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    /** Portée d'explosion des bombes */
    private static final int BOMB_RANGE = 3;
    
    /** Cooldown entre deux bombes (en nanosecondes) */
    private static final long BOMB_COOLDOWN = 2_000_000_000L;

    private final BombAnalyzer bombAnalyzer;
    private final PathFinder pathFinder;

    /**
     * Constructeur de la stratégie de mouvement.
     * 
     * @param bombAnalyzer Analyseur de bombes
     * @param pathFinder Chercheur de chemins
     */
    public MovementStrategy(BombAnalyzer bombAnalyzer, PathFinder pathFinder) {
        this.bombAnalyzer = bombAnalyzer;
        this.pathFinder = pathFinder;
    }

    /**
     * Calcule le mouvement optimal basé sur les priorités :
     * 1. Fuir le danger
     * 2. Utiliser A* pour se rapprocher de l'ennemi
     * 3. Mouvement sûr vers l'ennemi
     * 
     * @param currentRow Position actuelle (ligne)
     * @param currentCol Position actuelle (colonne)  
     * @param enemy Joueur ennemi
     * @return Tableau [deltaRow, deltaCol]
     */
    public int[] calculateOptimalMove(int currentRow, int currentCol, Player enemy) {
        // Priorité 1: Fuir si en danger
        if (bombAnalyzer.isDangerous(currentRow, currentCol)) {
            return findEscapeMove(currentRow, currentCol, enemy);
        }

        // Priorité 2: Utiliser A* pour se rapprocher
        List<Node> path = pathFinder.findPathToTarget(currentRow, currentCol, 
                                                     enemy.getRow(), enemy.getCol());
        if (path != null && path.size() > 1) {
            Node nextStep = path.get(1);
            return new int[]{nextStep.row - currentRow, nextStep.col - currentCol};
        }

        // Priorité 3: Mouvement sûr vers l'ennemi
        return findSafeMoveTowardsEnemy(currentRow, currentCol, enemy);
    }

    /**
     * Trouve un mouvement d'évasion en cas de danger.
     */
    private int[] findEscapeMove(int currentRow, int currentCol, Player enemy) {
        int[] safestPos = pathFinder.findSafeDirection(currentRow, currentCol, enemy, 15);
        return safestPos != null ? safestPos : new int[]{0, 0};
    }

    /**
     * Trouve un mouvement sûr vers l'ennemi en calculant les scores.
     */
    private int[] findSafeMoveTowardsEnemy(int currentRow, int currentCol, Player enemy) {
        int bestScore = -1;
        int[] bestMove = new int[]{0, 0};
        int currentDist = manhattanDistance(currentRow, currentCol, enemy.getRow(), enemy.getCol());

        for (int[] dir : DIRECTIONS) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];

            if (bombAnalyzer.isValidPosition(newRow, newCol) && 
                !bombAnalyzer.isDangerous(newRow, newCol)) {
                
                int newDist = manhattanDistance(newRow, newCol, enemy.getRow(), enemy.getCol());
                int score = currentDist - newDist;

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = dir;
                }
            }
        }
        return bestMove;
    }

    /**
     * Détermine si le bot doit poser une bombe.
     * Conditions : cooldown respecté, ennemi dans portée, possibilité d'évasion.
     * 
     * @param currentRow Position actuelle (ligne)
     * @param currentCol Position actuelle (colonne)
     * @param enemy Joueur ennemi
     * @param currentTime Temps actuel
     * @param lastBombTime Temps de la dernière bombe
     * @return true si une bombe doit être posée
     */
    public boolean shouldPlaceBomb(int currentRow, int currentCol, Player enemy, 
                                 long currentTime, long lastBombTime) {
        return (currentTime - lastBombTime >= BOMB_COOLDOWN) &&
               isEnemyInBombRange(currentRow, currentCol, enemy) &&
               canEscapeAfterBomb(currentRow, currentCol, enemy);
    }

    /**
     * Vérifie si l'ennemi est dans la portée d'explosion.
     */
    private boolean isEnemyInBombRange(int bombRow, int bombCol, Player enemy) {
        int enemyRow = enemy.getRow();
        int enemyCol = enemy.getCol();

        // Vérification horizontale et verticale
        if ((bombRow == enemyRow && Math.abs(bombCol - enemyCol) <= BOMB_RANGE) ||
            (bombCol == enemyCol && Math.abs(bombRow - enemyRow) <= BOMB_RANGE)) {
            return !bombAnalyzer.hasWallBetween(bombRow, bombCol, enemyRow, enemyCol);
        }
        return false;
    }

    /**
     * Vérifie si le bot peut s'échapper après avoir posé une bombe.
     * Simule temporairement la bombe sur la carte.
     */
    private boolean canEscapeAfterBomb(int bombRow, int bombCol, Player enemy) {
        char[][] mapData = bombAnalyzer.getMapData();
        char originalCell = mapData[bombRow][bombCol];

        // Simulation temporaire de la bombe
        mapData[bombRow][bombCol] = 'X';
        
        try {
            int[] escapeDirection = findEscapeMove(bombRow, bombCol, enemy);
            return escapeDirection[0] != 0 || escapeDirection[1] != 0;
        } finally {
            mapData[bombRow][bombCol] = originalCell; // Restauration
        }
    }

    /**
     * Calcule la distance de Manhattan entre deux points.
     * 
     * @param row1 Ligne du premier point
     * @param col1 Colonne du premier point
     * @param row2 Ligne du second point
     * @param col2 Colonne du second point
     * @return Distance de Manhattan
     */
    private int manhattanDistance(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }
}