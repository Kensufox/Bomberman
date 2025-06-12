package com.game.models.entities.bot;

import com.game.models.map.GameMap;

import java.util.*;

/**
 * Chercheur de chemins utilisant l'algorithme A*.
 * Composant du modèle pour la navigation intelligente.
 * 
 * @author RADJOU Dinesh
 * @version 4.0
 * @since 2025-06-08
 */


public class PathFinder {
    /** Directions de déplacement possibles */
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    private final GameMap gameMap;
    private final BombAnalyzer bombAnalyzer;

    /**
     * Constructeur du chercheur de chemins.
     * 
     * @param gameMap Carte de jeu
     * @param bombAnalyzer Analyseur de bombes
     */
    public PathFinder(GameMap gameMap, BombAnalyzer bombAnalyzer) {
        this.gameMap = gameMap;
        this.bombAnalyzer = bombAnalyzer;
    }

    /**
     * Trouve le chemin optimal vers une cible en utilisant A*.
     * 
     * @param startRow Position de départ (ligne)
     * @param startCol Position de départ (colonne)
     * @param targetRow Position cible (ligne)
     * @param targetCol Position cible (colonne)
     * @return Liste des nœuds du chemin, null si aucun chemin
     */
    public List<Node> findPathToTarget(int startRow, int startCol, int targetRow, int targetCol) {
        Node start = new Node(startRow, startCol, 0, 
                            manhattanDistance(startRow, startCol, targetRow, targetCol));
        if(!isValidMove(startRow, startCol)) return null;
        
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::fCost));
        Set<String> closedSet = new HashSet<>();
        Map<String, Node> nodeMap = new HashMap<>();

        openSet.add(start);
        nodeMap.put(start.getKey(), start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            //System.out.println(current.col + " " + current.row);

            if (closedSet.contains(current.getKey())) continue;
            closedSet.add(current.getKey());

            // Destination atteinte
            if (current.row == targetRow && current.col == targetCol) {
                return reconstructPath(current);
            }

            exploreNeighbors(current, targetRow, targetCol, openSet, closedSet, nodeMap);
        }
        return null; // Aucun chemin trouvé
    }

    /**
     * Explore les voisins d'un nœud pour l'algorithme A*.
     */
    private void exploreNeighbors(Node current, int targetRow, int targetCol, 
                                  PriorityQueue<Node> openSet, Set<String> closedSet, 
                                  Map<String, Node> nodeMap) {
        for (int[] dir : DIRECTIONS) {
            int newRow = current.row + dir[0];
            int newCol = current.col + dir[1];
            String neighborKey = newRow + "," + newCol;

            if (!isValidMove(newRow, newCol) || closedSet.contains(neighborKey)) continue;

            int gCost = current.gCost + 1;
            int hCost = manhattanDistance(newRow, newCol, targetRow, targetCol);

            Node neighbor = nodeMap.get(neighborKey);
            if (neighbor == null || gCost < neighbor.gCost) {
                neighbor = new Node(newRow, newCol, gCost, hCost);
                neighbor.parent = current;
                nodeMap.put(neighborKey, neighbor);
                openSet.add(neighbor);
            }
        }
    }



    /**
     * Reconstruit le chemin depuis le nœud final.
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
     * Vérifie si un mouvement est valide (sûr et traversable).
     */
    private boolean isValidMove(int row, int col) {
        return bombAnalyzer.isTraversable(row, col) && !bombAnalyzer.isDangerous(row, col);
    }

    /**
     * Vérifie si une position est valide pour l'évasion.
     */
    private boolean isValidForEscape(int row, int col) {
        return bombAnalyzer.isValidPosition(row, col) && 
               !bombAnalyzer.isWall(row, col) && 
               !bombAnalyzer.isOnBomb(row, col);
    }

    /**
     * Calcule la distance de Manhattan entre deux points.
     */
    private int manhattanDistance(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }
}