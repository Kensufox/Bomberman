package com.game.controllers;

import com.game.models.entities.BotPlayer;
import com.game.models.entities.Player;
import javafx.animation.AnimationTimer;


public class GameMapControllerbot extends GameMapController {
    public GameMapControllerbot() {
        super();
    }

    @Override
    protected Player[] createPlayers(){
        Player player1 = new Player(1, 1, Player.State.ALIVE);
        Player player2 = new BotPlayer(11, 13, Player.State.ALIVE, gameMap);
        return new Player[] { player1, player2 };
    }

    @Override
    protected void startMovementLoop() {
        AnimationTimer movementLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (PlayerContext ctx : players) {
                    Player p = ctx.player;

                    // Gestion expiration des pouvoirs
                    if (p.getPower() != null && now >= p.getPowerEndTime()) {
                        p.removePower();
                    }

                    if (p.getState() == Player.State.DEAD) continue;

                    int dRow = 0, dCol = 0;

                    if (p instanceof BotPlayer bot) {
                        if (bot.canMove(now)) {
                            Player enemy = players.get(0).player; // Le joueur humain



                            System.out.println(bot.getDebugInfo());
                            //System.out.println(bot.validateState());
                            // Décider du placement de bombe (séparément du mouvement)
//                            if (bot.shouldPlaceBomb(now, enemy)) {
//                                bomb.tryPlaceBomb(bot.getRow(), bot.getCol());
//                                System.out.println("Bot placing bomb at: [" + bot.getRow() + ", " + bot.getCol() + "]");
//                            }
                            System.out.println(now);
                            // Décider du mouvement
                            int[] move = bot.decideMove(now, enemy);

                            // Debug: afficher les décisions du bot
                           // System.out.println("Bot decision - Move: [" + move[0] + ", " + move[1] + "]");


                            System.out.println("\n");

                            // Effectuer le mouvement
                            if (move[0] != 0 || move[1] != 0) {
                                movePlayerIfPossible(bot, ctx.cell, move[0], move[1]);
                                bot.updateLastMoveTime(now);
                            }

                        }
                    } else {
                        // Contrôles du joueur humain
                        if (pressedKeys.contains(ctx.controls.up)) dRow = -1;
                        else if (pressedKeys.contains(ctx.controls.down)) dRow = 1;
                        else if (pressedKeys.contains(ctx.controls.left)) dCol = -1;
                        else if (pressedKeys.contains(ctx.controls.right)) dCol = 1;

                        if ((dRow != 0 || dCol != 0) && p.canMove(now)) {
                            movePlayerIfPossible(p, ctx.cell, dRow, dCol);
                            p.updateLastMoveTime(now);
                        }
                    }

                    ctx.cell.toFront();
                }
            }
        };

        movementLoop.start();
    }
}