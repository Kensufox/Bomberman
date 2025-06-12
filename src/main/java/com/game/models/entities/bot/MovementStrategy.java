package com.game.models.entities.bot;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de mouvement coordonnant l'analyse et la recherche de chemin.
 * Implémente la logique métier du bot (Modèle dans MVC).
 * Version améliorée avec détection d'impasses et système de scoring multi-critères.
 *
 * @author RADJOU Dinesh G2-5
 * @version 4.1
 * @since 2025-06-05
 */
public class MovementStrategy {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final int BOMB_RANGE = Bomb.getOriginalRange();
    private static final long BOMB_COOLDOWN = 2_000_000_000L;

    private final BombAnalyzer bombAnalyzer;
    private final PathFinder pathFinder;

    /**
     * Initialise une stratégie de mouvement avec l'analyseur de bombes et le chercheur de chemin spécifiés,
     * ainsi que des paramètres de configuration.
     *
     * @param bombAnalyzer           L'instance de BombAnalyzer utilisée pour évaluer le danger et les données de la carte.
     * @param pathFinder             L'instance de PathFinder utilisée pour le calcul des chemins.
     */
    public MovementStrategy(BombAnalyzer bombAnalyzer, PathFinder pathFinder) {
        this.bombAnalyzer = bombAnalyzer;
        this.pathFinder = pathFinder;
    }

    /**
     * Calcule le mouvement optimal basé sur les priorités :
     * 1. Fuir le danger (avec détection d'impasses améliorée)
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
        if (bombAnalyzer.isDangerous(currentRow, currentCol) ||
                manhattanDistance(currentRow, currentCol, enemy.getRow(), enemy.getCol()) < 2) {
            return findImprovedEscapeMove(currentRow, currentCol, enemy);
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
     * Méthode d'évasion améliorée qui évite les impasses.
     * Remplace l'ancienne méthode findEscapeMove qui utilisait pathFinder.findSafeDirection.
     */
    int[] findImprovedEscapeMove(int currentRow, int currentCol, Player enemy) {
        List<MoveOption> moveOptions = new ArrayList<>();

        // Évaluer chaque direction possible
        for (int[] dir : DIRECTIONS) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];

            if (bombAnalyzer.isValidPosition(newRow, newCol) &&
                    bombAnalyzer.isTraversable(newRow, newCol)) {

                MoveOption option = evaluateMoveOption(newRow, newCol, enemy, dir);
                moveOptions.add(option);
            }
        }

        // Trier par score (meilleur score en premier)
        moveOptions.sort((a, b) -> Integer.compare(b.score, a.score));

        // Retourner la meilleure option ou rester sur place
        return moveOptions.isEmpty() ? new int[]{0, 0} : moveOptions.get(0).direction;
    }



    /**
     * Évalue une option de mouvement avec plusieurs critères
     */
    private MoveOption evaluateMoveOption(int row, int col, Player enemy, int[] direction) {
        int score = 0;

        // Critère 1: Sécurité immédiate (priorité maximale)
        if (bombAnalyzer.isDangerous(row, col)) {
            score -= 1000; // Très mauvais
        } else {
            score += 100; // Bonus pour sécurité
        }

        // Critère 2: Détection d'impasse (critique)
        int escapeRoutes = countEscapeRoutes(row, col);
        if (escapeRoutes == 0) {
            score -= 800; // Très mauvais - impasse totale
        } else if (escapeRoutes == 1) {
            score -= 400; // Mauvais - risque d'être piégé
        } else {
            score += escapeRoutes * 50; // Bonus pour plusieurs sorties
        }

        // Critère 3: Distance de l'ennemi (pour éviter les bombes)
        int distanceFromEnemy = manhattanDistance(row, col, enemy.getRow(), enemy.getCol());
        if (distanceFromEnemy < 3) {
            score += distanceFromEnemy * 30; // Bonus pour s'éloigner
        }

        // Critère 4: Danger futur (bombes actives)
        int dangerScore = bombAnalyzer.getDangerScore(row, col);
        score -= dangerScore; // Moins il y a de danger, mieux c'est

        // Critère 5: Éviter les coins et bords de carte
        if (isNearEdge(row, col)) {
            score -= 50; // Malus léger pour les bords
        }

        return new MoveOption(direction, score, escapeRoutes);
    }

    /**
     * Compte le nombre de routes d'évasion disponibles depuis une position
     * en explorant sur 2-3 cases de profondeur
     */
    public int countEscapeRoutes(int row, int col) {
        int escapeRoutes = 0;

        for (int[] dir : DIRECTIONS) {
            if (hasEscapePathInDirection(row, col, dir[0], dir[1], 4)) {
                escapeRoutes++;
            }
        }

        return escapeRoutes;
    }

    /**
     * Vérifie s'il existe un chemin d'évasion dans une direction donnée
     */
    private boolean hasEscapePathInDirection(int startRow, int startCol, int dirRow, int dirCol, int depth) {
        int currentRow = startRow;
        int currentCol = startCol;

        for (int step = 1; step <= depth; step++) {
            currentRow += dirRow;
            currentCol += dirCol;

            if (!bombAnalyzer.isValidPosition(currentRow, currentCol) ||
                    !bombAnalyzer.isTraversable(currentRow, currentCol)) {
                return false;
            }

            // Si on trouve une case sûre à partir du step 2, c'est une route valide
            if (step >= 2 && !bombAnalyzer.isDangerous(currentRow, currentCol)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Vérifie si une position est près d'un bord de carte
     */
    private boolean isNearEdge(int row, int col) {
        char[][] mapData = bombAnalyzer.getMapData();
        return row <= 1 || col <= 1 ||
                row >= mapData.length - 2 ||
                col >= mapData[0].length - 2;
    }

    /**
     * Trouve un mouvement sûr vers l'ennemi en calculant les scores.
     * Version améliorée qui évite les impasses même en mode attaque.
     */
    private int[] findSafeMoveTowardsEnemy(int currentRow, int currentCol, Player enemy) {
        int bestScore = -1;
        int[] bestMove = new int[]{0, 0};
        int currentDist = manhattanDistance(currentRow, currentCol, enemy.getRow(), enemy.getCol());

        for (int[] dir : DIRECTIONS) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];

            if (bombAnalyzer.isValidPosition(newRow, newCol) &&
                    bombAnalyzer.isTraversable(newRow, newCol) &&
                    !bombAnalyzer.isDangerous(newRow, newCol)) {

                int newDist = manhattanDistance(newRow, newCol, enemy.getRow(), enemy.getCol());
                int score = currentDist - newDist;

                // Bonus pour éviter les impasses même en mode attaque
                int escapeRoutes = countEscapeRoutes(newRow, newCol);
                if (escapeRoutes > 1) {
                    score += 2; // Léger bonus pour la mobilité
                }

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
     *
     * @param currentRow Position actuelle du bot (ligne)
     * @param currentCol Position actuelle du bot (colonne)
     * @param enemy Joueur ennemi
     * @param currentTime Temps actuel en nanosecondes
     * @param lastBombTime Temps en nanosecondes du dernier placement de bombe
     * @return true si le bot doit poser une bombe, false sinon
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
    public boolean isEnemyInBombRange(int bombRow, int bombCol, Player enemy) {
        int enemyRow = enemy.getRow();
        int enemyCol = enemy.getCol();

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
    public boolean canEscapeAfterBomb(int bombRow, int bombCol, Player enemy) {
        char[][] mapData = bombAnalyzer.getMapData();
        char originalCell = mapData[bombRow][bombCol];

        // Simulation temporaire de la bombe
        mapData[bombRow][bombCol] = 'X';

        try {
            // Utiliser la nouvelle méthode d'évasion améliorée
            int[] escapeDirection = findImprovedEscapeMove(bombRow, bombCol, enemy);
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
    public int manhattanDistance(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    /**
     * Classe interne pour représenter une option de mouvement
     */
    private static class MoveOption {
        final int[] direction;
        final int score;
        final int escapeRoutes;

        MoveOption(int[] direction, int score, int escapeRoutes) {
            this.direction = direction;
            this.score = score;
            this.escapeRoutes = escapeRoutes;
        }
    }
}