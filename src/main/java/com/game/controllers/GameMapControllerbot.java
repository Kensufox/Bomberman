package com.game.controllers;

import com.game.models.entities.Player;
import com.game.models.entities.bot.BotPlayer;
import javafx.animation.AnimationTimer;

/**
 * Contrôleur pour la gestion de la carte du jeu avec un bot.
 * Cette classe étend {@link GameMapController} et surcharge certaines méthodes pour adapter le comportement du bot.
 */
public class GameMapControllerbot extends GameMapController {

    private String botDifficulty = "MEDIUM"; // Difficulté par défaut

    /**
     * Constructeur de la classe {@link GameMapControllerbot}.
     * Appelle le constructeur de la classe parente {@link GameMapController}.
     */
    public GameMapControllerbot() {
        super();
    }

    /**
     * Définit la difficulté du bot.
     * @param difficulty La difficulté : "EASY", "MEDIUM", ou "HARD"
     */
    public void setBotDifficulty(String difficulty) {
        this.botDifficulty = difficulty;
    }

    /**
     * Récupère la difficulté du bot.
     * @return La difficulté actuelle du bot
     */
    public String getBotDifficulty() {
        return this.botDifficulty;
    }

    /**
     * Crée et initialise les joueurs du jeu.
     * Un joueur humain et un bot sont créés et ajoutés à la liste des joueurs.
     * La difficulté du bot est configurée selon le paramètre défini.
     *
     * @return Un tableau contenant les joueurs créés : un joueur humain et un bot.
     */
    @Override
    protected Player[] createPlayers() {
        Player player1 = new Player(1, 1, Player.State.ALIVE); // Le joueur humain
        BotPlayer player2 = new BotPlayer(11, 13, Player.State.ALIVE, gameMap); // Le bot

        // Configurer la difficulté du bot
        configureBotDifficulty(player2);

        return new Player[]{player1, player2};
    }

    /**
     * Configure les paramètres du bot selon la difficulté choisie.
     * @param bot Le bot à configurer
     */
    private void configureBotDifficulty(BotPlayer bot) {
        switch (botDifficulty) {
            case "EASY":
                // Bot facile : plus lent, moins intelligent
                bot.setMoveDelay(500_000_000L); // 500ms entre les mouvements
                bot.setBombDelay(3); // 3 secondes entre les bombes
                bot.setIntelligenceLevel(1); // Niveau d'intelligence bas
                break;
            case "MEDIUM":
                // Bot moyen : vitesse normale
                bot.setMoveDelay(300_000_000L); // 300ms entre les mouvements
                bot.setBombDelay(2); // 2 secondes entre les bombes
                bot.setIntelligenceLevel(2); // Niveau d'intelligence moyen
                break;
            case "HARD":
                // Bot difficile : rapide et intelligent
                bot.setMoveDelay(150_000_000L); // 150ms entre les mouvements
                bot.setBombDelay(1); // 1 seconde entre les bombes
                bot.setIntelligenceLevel(3); // Niveau d'intelligence élevé
                break;
            default:
                // Difficulté par défaut (MEDIUM)
                bot.setMoveDelay(300_000_000L);
                bot.setBombDelay(2);
                bot.setIntelligenceLevel(2);
                break;
        }
    }

    /**
     * Démarre la boucle de mouvement des joueurs.
     * Cette boucle utilise {@link AnimationTimer} pour mettre à jour les actions des joueurs et du bot à chaque frame.
     */
    @Override
    protected void startMovementLoop() {
        AnimationTimer movementLoop = new AnimationTimer() {

            /**
             * Gère les actions de chaque joueur à chaque image (frame).
             * Vérifie l'état des pouvoirs, gère le mouvement et place les bombes pour le bot.
             *
             * @param now Le temps actuel en nanosecondes.
             */
            @Override
            public void handle(long now) {
                for (PlayerContext ctx : players) {
                    Player p = ctx.player;

                    // Gestion de l'expiration des pouvoirs
                    if (p.getPower() != null && now >= p.getPowerEndTime()) {
                        p.removePower(bomb); // Supprimer le pouvoir lorsque son temps est écoulé
                    }

                    // Si le joueur est mort, on passe à l'itération suivante
                    if (p.getState() == Player.State.DEAD) continue;

                    int dRow = 0, dCol = 0;

                    // Si le joueur est un bot, décide de ses actions
                    if (p instanceof BotPlayer bot) {
                        if (bot.canMove(now)) {
                            Player enemy = players.get(0).player; // Le joueur humain

                            if (enemy.getState() == Player.State.DEAD) continue;

                            // Debug des informations du bot (optionnel)
                            if(bot.getState() == Player.State.ALIVE && enemy.getState() == Player.State.ALIVE) {
                                // System.out.println("Bot Difficulty: " + botDifficulty);
                                //System.out.println(bot.getDebugInfo() + "\n");

                                /* Debug de la carte (décommenté si nécessaire)
                                for (int i = 0; i < gameMap.getMapData().length; i++) {
                                    for (int j = 0; j < gameMap.getMapData()[i].length; j++) {
                                        System.out.print(gameMap.getMapData()[i][j] + " ");
                                    }
                                    System.out.println();
                                }
                                */
                            }

                            // Décision d'action du bot : mouvement et placement de bombe
                            int[] action = bot.decideAction(System.nanoTime(), enemy);
                            dRow = action[0];
                            dCol = action[1];

                            boolean placeBomb = action[2] == 1;

                            // Si le bot doit poser une bombe, on la place
                            if (placeBomb) {
                                p.tryPlaceBomb(bot.getRow(), bot.getCol(), bomb);
                                bot.setLastBombTime(now);
                                //System.out.println("Bot placing bomb at: [" + bot.getRow() + ", " + bot.getCol() + "] (Difficulty: " + botDifficulty + ")");
                            }

                            // Effectuer le mouvement si nécessaire
                            if (dRow != 0 || dCol != 0) {
                                movePlayerIfPossible(bot, ctx.cell, dRow, dCol);
                                bot.updateLastMoveTime(now);
                            }
                        }
                    } else {
                        // Contrôles du joueur humain : détection des touches appuyées
                        if (pressedKeys.contains(ctx.controls.up)) dRow = -1;
                        else if (pressedKeys.contains(ctx.controls.down)) dRow = 1;
                        else if (pressedKeys.contains(ctx.controls.left)) dCol = -1;
                        else if (pressedKeys.contains(ctx.controls.right)) dCol = 1;

                        // Si le joueur humain a appuyé sur une touche et peut se déplacer, on effectue le mouvement
                        if ((dRow != 0 || dCol != 0) && p.canMove(now)) {
                            movePlayerIfPossible(p, ctx.cell, dRow, dCol);
                            p.updateLastMoveTime(now);
                        }
                    }

                    // Mettre à jour l'affichage de la cellule du joueur (au premier plan)
                    ctx.cell.toFront();
                }
            }
        };

        // Démarrer la boucle de mouvement
        movementLoop.start();
    }
}