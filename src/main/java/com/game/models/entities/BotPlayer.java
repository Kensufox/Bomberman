//bot esquive bombe, se rapproche du joueur

package com.game.models.entities;

import com.game.models.map.GameMap;
import java.util.*;

/**
 * Bot Bomberman qui évite les bombes et utilise A* pour se rapprocher de l'ennemi.
 *
 * @author RADJOU Dinesh G2-5
 * @version 2.0
 */
public class BotPlayer extends Player {

    /** Délai entre les mouvements du bot (en nanosecondes) */
    private static final long MOVE_DELAY_NS = 400_000_000L; // 800ms

    /** Portée d'explosion des bombes */
    private static final int BOMB_RANGE = 2;

    /** Référence vers la carte de jeu */
    private final GameMap gameMap;

    private Player enemy;

    private long lastBombTime = 0;
    private final long bombCooldown = 1_500_000_000L;

    private int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Crée une nouvelle instance de BotPlayer.
     */
    public BotPlayer(int startRow, int startCol, State state, GameMap map) {
        super(startRow, startCol, state);
        this.gameMap = Objects.requireNonNull(map, "GameMap ne peut pas être null");
        this.moveDelay = MOVE_DELAY_NS;
    }

    /**
     * Calcule le mouvement optimal en utilisant A* pour éviter les bombes et se rapprocher de l'ennemi.
     */
    public int[] decideMove(long now, Player enemy) {
        Objects.requireNonNull(enemy, "L'ennemi ne peut pas être null");
        this.enemy = enemy;

        // PRIORITÉ ABSOLUE: Si on est directement sur une bombe, fuir immédiatement
        if (isOnBomb(getRow(), getCol())) {
            return findUrgentEscapeMove();
        }

        // Si on est dans une zone dangereuse, fuir
        if (isDangerousPosition(getRow(), getCol())) {
            return findEscapeMove();
        }

        // Utiliser A* pour trouver le chemin optimal vers l'ennemi
        List<Node> path = findPathToEnemy();

        if (path != null && path.size() > 1) {
            Node nextStep = path.get(1); // Le premier est notre position actuelle
            return new int[]{nextStep.row - getRow(), nextStep.col - getCol()};
        }

        // Si pas de chemin trouvé, choisir au hasard tant qu'il ne mene pas dans un endroit dangereux
        int max_search = directions.length - 1;
        int randomIndex = (int)(Math.random() * directions.length);
        int new_row = getRow() + directions[randomIndex][0];
        int new_col = getCol() + directions[randomIndex][1];
        while ((!isValidPosition(new_row, new_col)
                || isDangerousPosition(new_row, new_col)) && max_search > 0) {
            randomIndex = (int)(Math.random() * directions.length);
            new_row = getRow() + directions[randomIndex][0];
            new_col = getCol() + directions[randomIndex][1];
            max_search--;
        }

        //si aucune pos trouvé alors, on essaye la position de base ou le joueur ne bouge
        if (max_search == 0 && (!isValidPosition(getRow(), getCol())
                || isDangerousPosition(getRow(), getCol()))){
            return new int[]{0, 0};
        }
        return directions[randomIndex];
    }

    /**
     * Vérifie si le bot est directement sur une bombe.
     */
    private boolean isOnBomb(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        return mapData[row][col] == 'X';
    }

    /**
     * Trouve un mouvement d'évasion d'urgence quand on est sur une bombe.
     * Accepte même les positions légèrement dangereuses pour survivre.
     */
    private int[] findUrgentEscapeMove() {
        // Premiere priorité: recherche récursive d'une zone sûre
        for (int[] dir : directions) {
            if (canReachSafeZoneInSteps(getRow() + dir[0], getCol() + dir[1], 6)) {
                return dir;
            }
        }

        // Deuxieme priorité: n'importe quelle position valide (même dangereuse)
        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];

            if (isValidPosition(newRow, newCol)) {
                return dir;
            }
        }

        return new int[]{0, 0};
    }

    private boolean canReachSafeZoneInSteps(int startRow, int startCol, int maxSteps) {
        if (maxSteps <= 0 || !isValidPosition(startRow, startCol)) return false;
        if (maxSteps <= 3 && isDangerousPosition(startRow, startCol)) return false;

        // Si cette position est déjà sûre
        if (maxSteps == 1 && !isDangerousPosition(startRow, startCol)) return true;

        // Sinon, chercher récursivement
        for (int[] dir : directions) {
            int newRow = startRow + dir[0];
            int newCol = startCol + dir[1];

            if (canReachSafeZoneInSteps(newRow, newCol, maxSteps - 1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Trouve un mouvement d'évasion avec recherche récursive si nécessaire.
     */
    private int[] findEscapeMove() {

        // recherche récursive de la position sûre la plus proche
        int[] safestPos = findSafestPosition(getRow(), getCol(), 5);
        if (safestPos != null) {
            // Retourne la direction vers cette position
            int deltaRow = Integer.compare(safestPos[0] - getRow(), 0);
            int deltaCol = Integer.compare(safestPos[1] - getCol(), 0);

//            System.out.println("POS :" + deltaRow + " " + deltaCol);
//            System.out.println("POS :" + Arrays.toString(safestPos));
//            System.out.println(isValidPosition(safestPos[0], getCol()) && !isOnBomb(safestPos[0], getCol()));
//            System.out.println(isValidPosition(getRow(), safestPos[1]) && !isOnBomb(getRow(), safestPos[1]));

            for (int[] direction : directions) {
                if (direction[0] == deltaRow && direction[1] == deltaCol) {
                    return new int[]{deltaRow, deltaCol};
                }
            }

            // Mouvement vertical d'abord, sinon horizontal
            if(isValidPosition(safestPos[0], getCol()) && !isOnBomb(safestPos[0], getCol()))  {
                //System.out.println(Arrays.toString(new int[]{deltaRow, getCol()}));
                return new int[]{deltaRow, 0};
            } else if (isValidPosition(getRow(), safestPos[1]) && !isOnBomb(getRow(), safestPos[1])) {
                //System.out.println(Arrays.toString(new int[]{0, deltaCol}));
                return new int[]{0, deltaCol};
            }

        }

        return new int[]{0, 0}; // Rester sur place en dernier recours
    }

    private int[] findSafestPosition(int startRow, int startCol, int maxDepth) {
        if (maxDepth <= 0) return null;

        int[] bestPos = null;
        int maxDistanceFromEnemy = -1;

        for (int[] dir : directions) {
            int newRow = startRow + dir[0];
            int newCol = startCol + dir[1];

            if (isValidPosition(newRow, newCol)) {
                if (!isDangerousPosition(newRow, newCol)) {
                    int distFromEnemy = manhattanDistance(newRow, newCol,  enemy.row, enemy.col);
                    if (distFromEnemy > maxDistanceFromEnemy) {
                        maxDistanceFromEnemy = distFromEnemy;
                        bestPos = new int[]{newRow, newCol};
                    }
                } else {
                    // Recherche récursive
                    int[] recursiveResult = findSafestPosition(newRow, newCol, maxDepth - 1);
                    if (recursiveResult != null) {
                        int distFromEnemy = manhattanDistance(recursiveResult[0], recursiveResult[1],  enemy.row, enemy.col);
                        if (distFromEnemy > maxDistanceFromEnemy) {
                            maxDistanceFromEnemy = distFromEnemy;
                            bestPos = recursiveResult;
                        }
                    }
                }
            }
        }
        return bestPos;
    }

    /**
     * Utilise l'algorithme A* pour trouver le chemin optimal vers l'ennemi.
     */
    private List<Node> findPathToEnemy() {
        Node start = new Node(getRow(), getCol(), 0, manhattanDistance(getRow(), getCol(), enemy.getRow(), enemy.getCol()));
        Node target = new Node(enemy.getRow(), enemy.getCol(), 0, 0);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost()));
        Set<String> closedSet = new HashSet<>();
        Map<String, Node> nodeMap = new HashMap<>();

        openSet.add(start);
        nodeMap.put(start.getKey(), start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = current.getKey();

            if (closedSet.contains(currentKey)) {
                continue;
            }
            closedSet.add(currentKey);

            // Si on a atteint la cible
            if (current.row == target.row && current.col == target.col) {
                return reconstructPath(current);
            }

            // Explorer les voisins
            for (int[] dir : directions) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                String neighborKey = newRow + "," + newCol;

                if ((!isValidPosition(newRow, newCol)) || closedSet.contains(neighborKey) || isDangerousPosition(newRow, newCol)) {
                    continue;
                }

                // à faire plus tard Coût plus élevé pour les positions dangereuses (mais pas impossible)
                //int moveCost = isDangerousPosition(newRow, newCol) ? 10 : 1;
                int gCost = current.gCost + 1;
                int hCost = manhattanDistance(newRow, newCol, enemy.getRow(), enemy.getCol());

                Node neighbor = nodeMap.get(neighborKey);
                if (neighbor == null || gCost < neighbor.gCost) {
                    neighbor = new Node(newRow, newCol, gCost, hCost);
                    neighbor.parent = current;
                    nodeMap.put(neighborKey, neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        return null; // Pas de chemin trouvé
    }

    /**
     * Reconstruit le chemin depuis le nœud final vers le début.
     */
    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }

        return path;
    }

    /**
     * Vérifie si une position est dangereuse (dans la zone d'explosion d'une bombe).
     */
    private boolean isDangerousPosition(int row, int col) {
        char[][] mapData = gameMap.getMapData();

        // Parcourir toutes les cases pour détecter les bombes
        for (int r = 0; r < mapData.length; r++) {
            for (int c = 0; c < mapData[0].length; c++) {
                if (mapData[r][c] == 'X') { // 'X' représente une bombe
                    if (isInExplosionRange(row, col, r, c, mapData)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Vérifie si une position est dans la zone d'explosion d'une bombe.
     */
    private boolean isInExplosionRange(int targetRow, int targetCol, int bombRow, int bombCol, char[][] mapData) {
        // Position de la bombe elle-même
        if (targetRow == bombRow && targetCol == bombCol) {
            return true;
        }

        // Explosion horizontale (même ligne)
        if (targetRow == bombRow) {
            int distance = Math.abs(targetCol - bombCol);
            return distance <= BOMB_RANGE && !hasWallBetween(bombRow, bombCol, targetRow, targetCol, mapData);
        }

        // Explosion verticale (même colonne)
        if (targetCol == bombCol) {
            int distance = Math.abs(targetRow - bombRow);
            return distance <= BOMB_RANGE && !hasWallBetween(bombRow, bombCol, targetRow, targetCol, mapData);
        }

        return false;
    }

    /**
     * Vérifie s'il y a un mur entre deux positions.
     */
    private boolean hasWallBetween(int fromRow, int fromCol, int toRow, int toCol, char[][] mapData) {
        // Mouvement horizontal
        if (fromRow == toRow) {
            int startCol = Math.min(fromCol, toCol);
            int endCol = Math.max(fromCol, toCol);
            for (int col = startCol + 1; col < endCol; col++) {
                if (isWall(fromRow, col)) {
                    return true;
                }
            }
        }
        // Mouvement vertical
        else if (fromCol == toCol) {
            int startRow = Math.min(fromRow, toRow);
            int endRow = Math.max(fromRow, toRow);
            for (int row = startRow + 1; row < endRow; row++) {
                if (isWall(row, fromCol)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Vérifie si une position est valide et traversable.
     */
    private boolean isValidPosition(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length) {
            return false;
        }

        char cell = mapData[row][col];
        return cell == '.' || cell == 'P'; // Cases vides ou power-ups
    }

    /**
     * Calcule la distance de Manhattan entre deux positions.
     */
    private int manhattanDistance(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    /**
     * Classe interne représentant un nœud pour l'algorithme A*.
     */
    private static class Node {
        final int row, col;
        final int gCost; // Coût depuis le début
        final int hCost; // Heuristique (distance à la cible)
        Node parent;

        Node(int row, int col, int gCost, int hCost) {
            this.row = row;
            this.col = col;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        int fCost() {
            return gCost + hCost;
        }

        String getKey() {
            return row + "," + col;
        }
    }


    private boolean isWall(int row, int col) {
        if (!isValidPosition(row, col)) {
            return true; // Hors limites = mur
        }

        char cell = gameMap.getMapData()[row][col];
        return cell == 'B' || cell == 'W'; // Seulement les vrais murs, pas les bombes
    }

    /**
     * Retourne des informations de débogage.
     */
    public String getDebugInfo() {
        return String.format("BotPlayer[pos=(%d,%d), onBomb=%b, danger=%b]",
                getRow(), getCol(), isOnBomb(getRow(), getCol()), isDangerousPosition(getRow(), getCol()));
    }



}