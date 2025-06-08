package com.game.models.entities.bot;

import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import com.game.utils.GameData;
import java.util.Objects;

/**
 * Contrôleur du Bot Bomberman implémentant une IA avancée.
 * Cette clasee étend {@link Player} pour lui ajouter une certaine intelligence
 * Sépare la logique métier (modèle) du contrôle (architecture MVC).
 *
 * @author RADJOU Dinesh G2-5
 * @version 4.0
 * @since 2025-06-05
 */


public class BotPlayer extends Player {
    
    /** Composants de l'architecture MVC - Modèle */
    private final GameMap gameMap;
    private final BombAnalyzer bombAnalyzer;
    private final PathFinder pathFinder;
    private final MovementStrategy movementStrategy;
    
    /** État du bot */
    private Player enemy;
    private long lastBombTime = 0;

    /**
     * Constructeur du bot player.
     * Initialise tous les composants nécessaires à l'IA.
     * 
     * @param startRow Position initiale (ligne)
     * @param startCol Position initiale (colonne)
     * @param state État initial du joueur
     * @param map Carte de jeu
     * @throws NullPointerException si map est null
     */
    public BotPlayer(int startRow, int startCol, State state, GameMap map) {
        super(startRow, startCol, state);
        
        // Configuration du timing
        moveDelay = 350_000_000 / GameData.gameSpeed;
        this.originalMoveDelay = moveDelay;
        
        // Initialisation du modèle
        this.gameMap = Objects.requireNonNull(map, "GameMap ne peut pas être null");
        this.bombAnalyzer = new BombAnalyzer(gameMap);
        this.pathFinder = new PathFinder(gameMap, bombAnalyzer);
        this.movementStrategy = new MovementStrategy(bombAnalyzer, pathFinder);
    }

    /**
     * Décision principale du bot : mouvement ET pose de bombe.
     * Point d'entrée principal pour l'IA (Contrôleur dans MVC).
     * 
     * @param now Temps actuel du système
     * @param enemy Joueur ennemi
     * @return Tableau [deltaRow, deltaCol, shouldPlaceBomb] où shouldPlaceBomb = 1 si bombe
     * @throws NullPointerException si enemy est null
     */
    public int[] decideAction(long now, Player enemy) {
        this.enemy = Objects.requireNonNull(enemy, "L'ennemi ne peut pas être null");

        // Calcul du mouvement optimal via la stratégie
        int[] movement = movementStrategy.calculateOptimalMove(getRow(), getCol(), enemy);

        // Décision de pose de bombe
        boolean shouldBomb = movementStrategy.shouldPlaceBomb(
                getRow(), getCol(), enemy, now, lastBombTime);

        return new int[]{movement[0], movement[1], shouldBomb ? 1 : 0};
    }

    /**
     * Version legacy pour compatibilité - retourne seulement le mouvement.
     * 
     * @param now Temps actuel
     * @param enemy Joueur ennemi
     * @return Mouvement [deltaRow, deltaCol]
     */
    public int[] decideMove(long now, Player enemy) {
        int[] action = decideAction(now, enemy);
        return new int[]{action[0], action[1]};
    }

    /**
     * Vérifie si le bot doit poser une bombe maintenant.
     * 
     * @param now Temps actuel
     * @param enemy Joueur ennemi
     * @return true si une bombe doit être posée
     */
    public boolean shouldPlaceBomb(long now, Player enemy) {
        return movementStrategy.shouldPlaceBomb(getRow(), getCol(), enemy, now, lastBombTime);
    }

    /**
     * Met à jour le timestamp de la dernière bombe posée.
     * 
     * @param lastBombTime Timestamp de la dernière bombe
     */
    public void setLastBombTime(long lastBombTime) {
        this.lastBombTime = lastBombTime;
    }

    /**
     * Retourne le timestamp de la dernière bombe posée.
     * 
     * @return Timestamp de la dernière bombe
     */
    public long getLastBombTime() {
        return lastBombTime;
    }

    /**
     * Génère des informations de debug pour le développement.
     * Utile pour le monitoring et les tests.
     * 
     * @return Chaîne formatée avec les informations du bot
     */
    public String getDebugInfo() {
        return String.format("BotPlayer[pos=(%d,%d), onBomb=%b, danger=%b, lastBomb=%.1fs ago]",
                getRow(), getCol(),
                bombAnalyzer.isOnBomb(getRow(), getCol()),
                bombAnalyzer.isDangerous(getRow(), getCol()),
                (System.nanoTime() - lastBombTime) / 1_000_000_000.0);
    }

    /**
     * Retourne l'ennemi actuel.
     * 
     * @return Joueur ennemi
     */
    public Player getEnemy() {
        return enemy;
    }

    /**
     * Retourne l'analyseur de bombes pour accès externe si nécessaire.
     * 
     * @return Instance de BombAnalyzer
     */
    public BombAnalyzer getBombAnalyzer() {
        return bombAnalyzer;
    }

    /**
     * Retourne la stratégie de mouvement pour tests unitaires.
     * 
     * @return Instance de MovementStrategy
     */
    protected MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }
}