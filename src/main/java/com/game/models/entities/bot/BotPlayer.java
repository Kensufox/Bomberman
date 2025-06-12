package com.game.models.entities.bot;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import com.game.utils.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private char[][] map;
    private final BombAnalyzer bombAnalyzer;
    private final PathFinder pathFinder;
    private final MovementStrategy movementStrategy;
    
    /** État du bot */
    private Player enemy;
    private long lastBombTime = 0;
    private int intelligenceLevel;

    /**
     * Constructeur du bot player.
     * Initialise tous les composants nécessaires à l'IA.
     * 
     * @param startRow Position initiale (ligne)
     * @param startCol Position initiale (colonne)
     * @param state État initial du joueur
     * @param gameMap Carte de jeu
     * @throws NullPointerException si map est null
     */
    public BotPlayer(int startRow, int startCol, State state, GameMap gameMap) {
        super(startRow, startCol, state);
        
        // Configuration du timing
        moveDelay = 350_000_000 / GameData.getGameSpeed();
        this.originalMoveDelay = moveDelay;
        
        // Initialisation du modèle
        this.gameMap = Objects.requireNonNull(gameMap, "GameMap ne peut pas être null");
        this.bombAnalyzer = new BombAnalyzer(gameMap);
        this.pathFinder = new PathFinder(gameMap, bombAnalyzer);
        this.movementStrategy = new MovementStrategy(bombAnalyzer, pathFinder);

        this.map = gameMap.getMapData();
    }

    /**
     * Assigns the current bomb to the bomb analyzer.
     *
     * @param bomb the Bomb object to set
     */
    public void setBomb(Bomb bomb) {
        bombAnalyzer.setBomb(bomb);
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

        // Décision de pose de bombe
        boolean shouldBomb = movementStrategy.shouldPlaceBomb(
                getRow(), getCol(), enemy, now, lastBombTime);


        char originalCell = bombAnalyzer.getMapData()[getRow()][getCol()];
        if (shouldBomb)
            // Simulation temporaire de la bombe
            bombAnalyzer.getMapData()[getRow()][getCol()] = 'X';

        // Calcul du mouvement optimal via la stratégie
        int[] movement = movementStrategy.calculateOptimalMove(getRow(), getCol(), enemy);
        bombAnalyzer.getMapData()[getRow()][getCol()] = originalCell;


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

//    /**
//     * Génère des informations de debug pour le développement.
//     * Utile pour le monitoring et les tests.
//     *
//     * @return Chaîne formatée avec les informations du bot
//     */
//    public String getDebugInfo() {
//        return String.format("BotPlayer[pos=(%d,%d), onBomb=%b, danger=%b, lastBomb=%.1fs ago]",
//                getRow(), getCol(),
//                bombAnalyzer.isOnBomb(getRow(), getCol()),
//                bombAnalyzer.isDangerous(getRow(), getCol()),
//                (System.nanoTime() - lastBombTime) / 1_000_000_000.0);
//    }

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


    /**
     * Sets the intelligence level.
     *
     * @param level the new intelligence level
     */
    public void setIntelligenceLevel(int level) {
        this.intelligenceLevel = level;
    }

    /**
     * Returns the current intelligence level.
     *
     * @return the intelligence level
     */
    public int getIntelligenceLevel() {
        return intelligenceLevel;
    }



    /**
     * Génère des informations de debug complètes pour le développement.
     * Version améliorée avec analyse détaillée de l'état du bot.
     *
     * @return Chaîne formatée avec toutes les informations critiques du bot
     */
    public String getDebugInfo() {
        StringBuilder debug = new StringBuilder();
        long now = System.nanoTime();

        // === ÉTAT GÉNÉRAL ===
        debug.append("🤖 BOT STATUS ")
                .append("=".repeat(50))
                .append("\n");

        // Position et mouvement
        debug.append(String.format("📍 Position: (%d,%d) | Enemy: (%d,%d) | Distance: %d\n",
                getRow(), getCol(),
                enemy != null ? enemy.getRow() : -1,
                enemy != null ? enemy.getCol() : -1,
                enemy != null ? Math.abs(getRow() - enemy.getRow()) + Math.abs(getCol() - enemy.getCol()) : -1));

        // === ANALYSE DE SÉCURITÉ ===
        debug.append("\n🛡️  SAFETY ANALYSIS:\n");
        boolean isOnBomb = bombAnalyzer.isOnBomb(getRow(), getCol());
        boolean isDangerous = bombAnalyzer.isDangerous(getRow(), getCol());

        debug.append(String.format("   • On Bomb: %s%s%s\n",
                isOnBomb ? "🔥 YES" : "✅ NO",
                isOnBomb ? " (CRITICAL!)" : "",
                isOnBomb ? " 💀" : ""));

        debug.append(String.format("   • In Danger Zone: %s%s\n",
                isDangerous ? "⚠️  YES" : "✅ SAFE",
                isDangerous ? " (ESCAPE NEEDED!)" : ""));

        // Analyse des zones dangereuses
        int dangerousNeighbors = countDangerousNeighbors();
        debug.append(String.format("   • Dangerous Neighbors: %d/4 %s\n",
                dangerousNeighbors,
                getDangerLevelEmoji(dangerousNeighbors)));

        // === INFORMATIONS BOMBES ===
        debug.append("\n💣 BOMB SYSTEM:\n");
        double bombCooldownSec = (now - lastBombTime) / 1_000_000_000.0;
        boolean canBomb = bombCooldownSec >= getBombDelay();

        debug.append(String.format("   • Cooldown: %.2fs %s (%.1f%% ready)\n",
                bombCooldownSec,
                canBomb ? "✅ READY" : "⏳ COOLING",
                Math.min(100.0, (bombCooldownSec / 1.5) * 100)));

        if (enemy != null) {
            boolean enemyInRange = bombAnalyzer.isInExplosionRange(getRow(), getCol(), enemy.getRow(), enemy.getCol());
            boolean canEscape = movementStrategy.canEscapeAfterBomb(getRow(), getCol(), enemy);
            debug.append(String.format("   • Enemy in Range: %s\n", enemyInRange ? "🎯 YES" : "❌ NO"));
            debug.append(String.format("   • Can Escape After: %s\n", canEscape ? "✅ YES" : "⚠️  RISKY"));
            debug.append(String.format("   • Should Place Bomb: %s\n",
                    (canBomb && enemyInRange && canEscape) ? "💥 YES!" : "🚫 NO"));
        }

        // === STRATÉGIE ET PATHFINDING ===
        debug.append("\n🧭 STRATEGY & PATHFINDING:\n");
        if (enemy != null) {
            // Analyse du chemin vers l'ennemi
            List<Node> pathToEnemy = pathFinder.findPathToTarget(getRow(), getCol(),
                    enemy.getRow(), enemy.getCol());
            debug.append(String.format("   • Path to Enemy: %s (length: %d)\n",
                    pathToEnemy != null ? "🛤️  FOUND" : "🚫 BLOCKED",
                    pathToEnemy != null ? pathToEnemy.size() - 1 : -1));

            // Prochaine action prévue
            int[] nextMove = movementStrategy.calculateOptimalMove(getRow(), getCol(), enemy);
            String moveDirection = getMoveDirection(nextMove);
            debug.append(String.format("   • Next Move: %s %s\n",
                    moveDirection,
                    Arrays.equals(nextMove, new int[]{0, 0}) ? "(STAYING)" : "(MOVING)"));

            // Stratégie actuelle
            String currentStrategy = determineCurrentStrategy();
            debug.append(String.format("   • Current Strategy: %s\n", currentStrategy));
        }

        // === ANALYSE DE L'ENVIRONNEMENT ===
        debug.append("\n🗺️  ENVIRONMENT:\n");
        debug.append(String.format("   • Traversable Neighbors: %d/4\n", countTraversableNeighbors()));
        debug.append(String.format("   • Wall Neighbors: %d/4\n", countWallNeighbors()));
        debug.append(String.format("   • Escape Routes: %d\n", countEscapeRoutes()));

        // === PERFORMANCE ===
        debug.append("\n⚡ PERFORMANCE:\n");
        debug.append(String.format("   • Move Delay: %dms\n", moveDelay / 1_000_000));
        debug.append(String.format("   • Game Speed: %dx\n", com.game.utils.GameData.getGameSpeed()));
        debug.append(String.format("   • Uptime: %.1fs\n",
                (now - (lastBombTime > 0 ? lastBombTime - (long)(1.5 * 1_000_000_000) : now)) / 1_000_000_000.0));

        // === ALERTES CRITIQUES ===
        List<String> alerts = getCriticalAlerts();
        if (!alerts.isEmpty()) {
            debug.append("\n🚨 CRITICAL ALERTS:\n");
            for (String alert : alerts) {
                debug.append("   • ").append(alert).append("\n");
            }
        }

        debug.append("=".repeat(60));
        //System.out.println(debug.toString());
        return debug.toString();
    }

// === MÉTHODES UTILITAIRES POUR LE DEBUG ===

    /**
     * Compte le nombre de voisins dangereux autour du bot
     */
    private int countDangerousNeighbors() {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];
            if (bombAnalyzer.isValidPosition(newRow, newCol) &&
                    bombAnalyzer.isDangerous(newRow, newCol)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retourne l'emoji correspondant au niveau de danger
     */
    private String getDangerLevelEmoji(int dangerousNeighbors) {
        switch (dangerousNeighbors) {
            case 0: return "😌";
            case 1: return "😐";
            case 2: return "😰";
            case 3: return "😱";
            case 4: return "💀";
            default: return "❓";
        }
    }



    /**
     * Convertit un mouvement en direction lisible
     */
    private String getMoveDirection(int[] move) {
        if (move[0] == -1 && move[1] == 0) return "⬆️  UP";
        if (move[0] == 1 && move[1] == 0) return "⬇️  DOWN";
        if (move[0] == 0 && move[1] == -1) return "⬅️  LEFT";
        if (move[0] == 0 && move[1] == 1) return "➡️  RIGHT";
        return "⏸️  STAY";
    }

    /**
     * Détermine la stratégie actuelle du bot
     */
    private String determineCurrentStrategy() {
        if (bombAnalyzer.isDangerous(getRow(), getCol())) {
            return "🏃 ESCAPE MODE";
        } else if (enemy != null && bombAnalyzer.isInExplosionRange(getRow(), getCol(), enemy.getRow(), enemy.getCol()) && movementStrategy.canEscapeAfterBomb(getRow(), getCol(), enemy)) {
            return "🎯 ATTACK MODE";
        } else if (enemy != null) {
            return "🕵️  HUNT MODE";
        } else {
            return "⏳ WAIT MODE";
        }
    }

    /**
     * Compte les voisins traversables
     */
    private int countTraversableNeighbors() {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];
            if (bombAnalyzer.isTraversable(newRow, newCol)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte les murs voisins
     */
    private int countWallNeighbors() {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];
            if (bombAnalyzer.isWall(newRow, newCol)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte les routes d'évasion disponibles
     */
    private int countEscapeRoutes() {
        int count = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newRow = getRow() + dir[0];
            int newCol = getCol() + dir[1];
            if (bombAnalyzer.isValidPosition(newRow, newCol) &&
                    !bombAnalyzer.isDangerous(newRow, newCol) &&
                    !bombAnalyzer.isWall(newRow, newCol)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Génère la liste des alertes critiques
     */
    private List<String> getCriticalAlerts() {
        List<String> alerts = new ArrayList<>();

        if (bombAnalyzer.isOnBomb(getRow(), getCol())) {
            alerts.add("🔥 STANDING ON BOMB - IMMEDIATE ESCAPE REQUIRED!");
        }

        if (bombAnalyzer.isDangerous(getRow(), getCol()) && countEscapeRoutes() == 0) {
            alerts.add("💀 TRAPPED IN DANGER ZONE - NO ESCAPE ROUTES!");
        }

        if (countEscapeRoutes() <= 1) {
            alerts.add("⚠️  LIMITED MOBILITY - Only " + countEscapeRoutes() + " escape route(s)");
        }

        if (enemy != null && Math.abs(getRow() - enemy.getRow()) + Math.abs(getCol() - enemy.getCol()) <= 1) {
            alerts.add("👾 ENEMY ADJACENT - High risk situation!");
        }

        return alerts;
    }
}