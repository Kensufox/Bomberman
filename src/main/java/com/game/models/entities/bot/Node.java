// ===== Node.java =====
package com.game.models.entities.bot;

/**
 * Nœud pour l'algorithme A*.
 * Cette classe représente un point dans une grille 2D avec des informations
 * de coût utilisées par l'algorithme de pathfinding A*.
 * @author RADJOU Dinesh G2-5
 * @version 4.0
 * @since 2025-06-05
 */
public class Node {
    // Position du nœud dans la grille
    final int row, col;        // Coordonnées (ligne, colonne) - immutables

    // Coûts pour l'algorithme A*
    final int gCost;          // Coût réel depuis le nœud de départ (distance parcourue)
    final int hCost;          // Coût heuristique vers le nœud d'arrivée (estimation)

    // Référence vers le nœud parent dans le chemin optimal
    Node parent;              // Permet de reconstituer le chemin final

    /**
     * Constructeur d'un nœud A*.
     *
     * @param row   Ligne du nœud dans la grille
     * @param col   Colonne du nœud dans la grille
     * @param gCost Coût G (distance réelle depuis le départ)
     * @param hCost Coût H (heuristique vers l'arrivée)
     */
    public Node(int row, int col, int gCost, int hCost) {
        this.row = row;
        this.col = col;
        this.gCost = gCost;
        this.hCost = hCost;
        // Note: parent est initialisé à null par défaut
    }

    /**
     * Calcule le coût total F pour l'algorithme A*.
     * F = G + H (coût réel + estimation heuristique)
     *
     * @return Le coût total F utilisé pour prioriser les nœuds
     */
    public int fCost() {
        return gCost + hCost;
    }

    /**
     * Génère une clé unique pour identifier ce nœud.
     * Utilisé comme identifiant dans les structures de données (Map, Set).
     *
     * @return Une chaîne au format "ligne,colonne"
     */
    public String getKey() {
        return row + "," + col;
    }
}