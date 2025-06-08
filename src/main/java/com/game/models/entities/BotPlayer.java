//bot esquive bombe, se rapproche du joueur

package com.game.models.entities;

import com.game.models.map.GameMap;
import com.game.utils.GameData;

import java.util.*;

/**
 * Bot Bomberman qui évite les bombes et utilise A* pour se rapprocher de l'ennemi.
 *
 * @author RADJOU Dinesh G2-5
 * @version 2.0
 */
public class BotPlayer extends Player {

    /** Portée d'explosion des bombes */
    private static final int BOMB_RANGE = Bomb.getRange() + 1; // +1 pour inclure la case de la bombe elle-même

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
        moveDelay = 350_000_000/ GameData.gameSpeed;
        this.gameMap = Objects.requireNonNull(map, "GameMap ne peut pas être null");
        this.originalMoveDelay = moveDelay;
    }

    /**
     * Calcule le mouvement optimal en utilisant A* pour éviter les bombes et se rapprocher de l'ennemi.
     */
    public int[] decideMove(long now, Player enemy) {
        Objects.requireNonNull(enemy, "L'ennemi ne peut pas être null");
        this.enemy = enemy;

        // Si on est dans une zone dangereuse, fuir
        if (isDangerousPosition(getRow(), getCol())) {
            System.out.println("HELLLLLLLLPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
            return findEscapeMove();
        }

        // Utiliser A* pour trouver le chemin optimal vers l'ennemi
        List<Node> path = findPathToEnemy();

        if (path != null && path.size() > 1) {
            Node nextStep = path.get(1); // Le premier est notre position actuelle
            System.out.println("Next step: " + nextStep.row + ", " + nextStep.col);
            return new int[]{nextStep.row - getRow(), nextStep.col - getCol()};
        }

        // Recherche systématique
        int[] safeMove = findSafeMoveTowardsEnemy();
        if (safeMove != null) {
            return safeMove;
        }
        return new int[]{0, 0}; // Rester sur place si aucune option n'est trouvée
    }


    private int[] findSafeMoveTowardsEnemy() {
        int bestScore = -1;
        int[] bestMove = null;

        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];

            // Ne considérer que les positions sûres
            if (isValidPosition(newRow, newCol) && !isDangerousPosition(newRow, newCol)) {
                // Calculer si ce mouvement nous rapproche de l'ennemi
                int currentDist = manhattanDistance(getRow(), getCol(), enemy.getRow(), enemy.getCol());
                int newDist = manhattanDistance(newRow, newCol, enemy.getRow(), enemy.getCol());

                // Privilégier les mouvements qui nous rapprochent
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
     * Vérifie si le bot est directement sur une bombe.
     */
    private boolean isOnBomb(int row, int col) {
        char[][] mapData = gameMap.getMapData();
        return mapData[row][col] == 'X';
    }


    /**
     * Trouve un mouvement d'évasion avec recherche récursive si nécessaire.
     */
    private int[] findEscapeMove() {

        // recherche récursive de la position sûre la plus proche
        int[] safestPos = findSafestDirection(getRow(), getCol(), 15);
        if (safestPos != null) {

            System.out.println("POS :" + safestPos[0] + " " + safestPos[1]);


            for (int i = 0; i < gameMap.getMapData().length; i++) {
                for (int j = 0; j < gameMap.getMapData()[i].length; j++) {
                    System.out.print(gameMap.getMapData()[i][j] + " ");
                }
                System.out.println(); // Saut de ligne après chaque rangée
            }
            System.out.println(gameMap.getMapData()[getRow() + safestPos[0]][getCol() + safestPos[1]]);




            return safestPos; // Retourne la direction trouvée

        }

        return new int[]{0, 0}; // Rester sur place en dernier recours
    }

    private int[] findSafestDirection(int startRow, int startCol, int maxDepth) {
    Set<String> visited = new HashSet<>();
    return findSafestDirection(startRow, startCol, maxDepth, visited);
}

private int[] findSafestDirection(int startRow, int startCol, int maxDepth, Set<String> visited) {
    if (maxDepth <= 0) return null;
    
    String currentPos = startRow + "," + startCol;
    if (visited.contains(currentPos)) return null;
    
    visited.add(currentPos);
    
    int[] bestDirection = null;
    int maxDistanceFromEnemy = -1;

    for (int[] dir : directions) {
        int newRow = startRow + dir[0];
        int newCol = startCol + dir[1];

        if (isValidPosition(newRow, newCol) && !isWall(newRow, newCol) && 
            gameMap.getMapData()[newRow][newCol] != 'B' && !isOnBomb(newRow, newCol)) {
            
            if (!isDangerousPosition(newRow, newCol)) {
                int distFromEnemy = manhattanDistance(newRow, newCol, enemy.getRow(), enemy.getCol());
                if (distFromEnemy > maxDistanceFromEnemy) {
                    maxDistanceFromEnemy = distFromEnemy;
                    bestDirection = dir;
                }
            } else {
                // Recherche récursive avec mémorisation
                int[] recursiveDir = findSafestDirection(newRow, newCol, maxDepth - 1, visited);
                if (recursiveDir != null) {
                    int futureRow = newRow + recursiveDir[0];
                    int futureCol = newCol + recursiveDir[1];
                    int distFromEnemy = manhattanDistance(futureRow, futureCol, enemy.getRow(), enemy.getCol());
                    if (distFromEnemy > maxDistanceFromEnemy) {
                        maxDistanceFromEnemy = distFromEnemy;
                        bestDirection = dir;
                    }
                }
            }
        }
    }
    
    visited.remove(currentPos); // Backtrack
    return bestDirection;
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

                if ((!isValidPosition(newRow, newCol)) || gameMap.getMapData()[newRow][newCol] != '.' || closedSet.contains(neighborKey) || isDangerousPosition(newRow, newCol)) {
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
        if (isOnBomb(targetRow, targetCol)) {
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
        }return true;
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
        char[][] mapData = gameMap.getMapData();
        if (row < 0 || col < 0 || row >= mapData.length || col >= mapData[0].length) {
            return false;
        }

        char cell = gameMap.getMapData()[row][col];
        return cell == 'W'; // Seulement les vrais murs, pas les bombes
    }

    /**
     * Retourne des informations de débogage.
     */
    public String getDebugInfo() {
        return String.format("BotPlayer[pos=(%d,%d), onBomb=%b, danger=%b]",
                getRow(), getCol(), isOnBomb(getRow(), getCol()), isDangerousPosition(getRow(), getCol()));
    }



}