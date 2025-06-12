

package com.game.controllers;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.entities.PowerUp;
import com.game.models.entities.bot.BotPlayer;
import com.game.models.map.GameMap;
import com.game.utils.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller class responsible for managing the game map, including player movements,
 * bomb placements, power-up handling, and transitions to the game over screen.
 */
public class GameMapController {

    /**
     * Default constructor for GameMapController.
     * Initializes the controller instance.
     */
    public GameMapController() {
        // No additional initialization required
    }

    /** The primary grid representing game elements */
    @FXML protected GridPane mapGrid;

    /** The background grid behind the game map */
    @FXML protected GridPane backgroundGrid;

    /** Handles player inputs */
    protected InputHandler inputHandler;

    /** Represents the current power-up in the game */
    protected PowerUp powerUp;

    /** The graphical node of the active power-up */
    protected StackPane powerUpCell;

    /** Handles bomb-related logic */
    protected Bomb bomb;

    /** The current game map */
    protected GameMap gameMap;

    /** Tracks the currently pressed keys */
    protected final Set<KeyCode> pressedKeys = new HashSet<>();

    /** Stores the players and their visual/contextual data */
    protected final List<PlayerContext> players = new ArrayList<>();

    /** Active power-ups on the grid */
    final List<PowerUp> activePowerUps = new ArrayList<>();

    /** Visual nodes representing active power-ups */
    final List<StackPane> activePowerUpCells = new ArrayList<>();

    /** Inner class for storing a player, their cell, and controls */
    protected static class PlayerContext {
        final Player player;
        final StackPane cell;
        final InputHandler.PlayerControls controls;

        PlayerContext(Player player, StackPane cell, InputHandler.PlayerControls controls) {
            this.player = player;
            this.cell = cell;
            this.controls = controls;
        }
    }

    /**
     * Initializes the game map, players, bombs, and sets up input handling and the animation loop.
     */
    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();
        gameMap.setupBackground(gameMap, backgroundGrid);
        gameMap.setupMap(mapGrid);

        // Create player images
        Image player1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Player1)));
        Image player2Img;

        if (!(this instanceof GameMapControllerbot)) {
            player2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Player2)));
        } else {
            player2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.RobotPlayer)));
        }

        // Create players
        Player[] players_temps = createPlayers();
        Player player1 = players_temps[0];
        Player player2 = players_temps[1];


        // Create graphical nodes
        StackPane player1Cell = ResourceLoader.createPixelatedImageNode(player1Img, gameMap.getTileSize(), gameMap.getTileSize() * 1.75, 0, 15);
        StackPane player2Cell = ResourceLoader.createPixelatedImageNode(player2Img, gameMap.getTileSize(), gameMap.getTileSize() * 1.75, 0, 15);

        // Add players to the list with their controls
        players.add(new PlayerContext(player1, player1Cell, inputHandler.getJ1Controls()));
        players.add(new PlayerContext(player2, player2Cell, inputHandler.getJ2Controls()));

        // Add players to the grid
        for (PlayerContext ctx : players) {
            mapGrid.add(ctx.cell, ctx.player.getCol(), ctx.player.getRow());
            ctx.cell.toFront();
        }

        // Initialize bomb
        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg(), 
            players.stream().map(pc -> pc.player).collect(Collectors.toList()), this);

        if(players.get(1).player instanceof BotPlayer){
            ((BotPlayer) players.get(1).player).setBomb(bomb);
        }

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPressed);
        mapGrid.setOnKeyReleased(this::handleKeyReleased);

        if (PlayerManager.hasCurrentPlayer()) {
            players.get(0).player.setPlayerConnected(true);
            PlayerManager.recordGamePlayed();
            //System.out.println("Partie démarrée pour: " + PlayerManager.getCurrentPlayerName());
        }

        startMovementLoop();
    }

    /**
     * Creates and returns two players initialized with default positions and states.
     * @return an array containing player 1 and player 2
     */
    protected Player[] createPlayers() {
        Player player1 = new Player(1, 1, Player.State.ALIVE);
        Player player2 = new Player(11, 13, Player.State.ALIVE);
        return new Player[] { player1, player2 };
    }

    /**
     * Handles key press events and bomb placements.
     * @param event the key press event
     */
    protected void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        if (!pressedKeys.contains(code)) {
            pressedKeys.add(code);
            // Check if the key corresponds to a bomb for any player
            for (PlayerContext ctx : players) {
                if (code == ctx.controls.bomb) {
                    ctx.player.tryPlaceBomb(ctx.player.getRow(), ctx.player.getCol(), bomb);
                }
            }
        }
    }

    /**
     * Handles key release events by updating the pressed key set.
     * @param event the key release event
     */
    protected void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    /**
     * Starts the animation loop responsible for moving players and applying power-up effects.
     */
    protected void startMovementLoop() {
        AnimationTimer movementLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (PlayerContext ctx : players) {
                    Player p = ctx.player;

                    // Handle power-up expiration
                    if (p.getPower() != null && now >= p.getPowerEndTime()) {
                        p.removePower(bomb);
                    }

                    if (p.getState() == Player.State.DEAD) continue;

                    int dRow = 0, dCol = 0;
                    if (pressedKeys.contains(ctx.controls.up)) dRow = -1;
                    else if (pressedKeys.contains(ctx.controls.down)) dRow = 1;
                    else if (pressedKeys.contains(ctx.controls.left)) dCol = -1;
                    else if (pressedKeys.contains(ctx.controls.right)) dCol = 1;

                    if ((dRow != 0 || dCol != 0) && p.canMove(now)) {
                        movePlayerIfPossible(p, ctx.cell, dRow, dCol);
                        p.updateLastMoveTime(now);
                    }

                    ctx.cell.toFront();
                }
            }
        };

        movementLoop.start();
    }

    /**
     * Moves the player in the given direction if the destination is walkable.
     * @param player the player to move
     * @param cell the StackPane representing the player on the grid
     * @param dRow the row direction offset
     * @param dCol the column direction offset
     */
    protected void movePlayerIfPossible(Player player, StackPane cell, int dRow, int dCol) {
        int oldRow = player.getRow();
        int oldCol = player.getCol();
        int newRow = oldRow + dRow;
        int newCol = oldCol + dCol;

        if (isWalkable(newRow, newCol)) {
            player.move(dRow, dCol);

            GridPane.setRowIndex(cell, player.getRow());
            GridPane.setColumnIndex(cell, player.getCol());
            SFXPlayer.play(SFXLibrary.STEP);
            checkPowerUpCollision(player);
        }
        cell.toFront();
    }

    /**
     * Determines whether a given map cell is walkable.
     * @param row the row index
     * @param col the column index
     * @return true if the cell is walkable, false otherwise
     */
    protected boolean isWalkable(int row, int col) {
        if (row < 0 || col < 0 || row >= gameMap.getMapData().length || col >= gameMap.getMapData()[0].length) {
            return false; // safety out of bounds
        }
        char cell = gameMap.getMapData()[row][col];
        return cell == '.' || cell == 'P';
    }

    /**
     * Marks a player as dead, removes them from the grid, and checks for a winner.
     * @param player the player to mark as dead
     */
    public void killPlayer(Player player) {
        if (player.getState() == Player.State.DEAD) return;

        player.setState(Player.State.DEAD);
        PlayerContext deadCtx = players.stream().filter(p -> p.player == player).findFirst().orElse(null);
        if (deadCtx != null) {
            mapGrid.getChildren().remove(deadCtx.cell);
        }

        // Check if there is only one player alive to declare the winner
        List<PlayerContext> alivePlayers = players.stream()
                .filter(p -> p.player.getState() == Player.State.ALIVE)
                .collect(Collectors.toList());

        if (alivePlayers.size() == 1) {
            // Déterminer quel joueur a gagné et incrémenter le bon score
            int winnerIndex = players.indexOf(alivePlayers.get(0));
            if (winnerIndex == 0) {
                ScoreManager.incrementP1Score();
            } else if (winnerIndex == 1) {
                ScoreManager.incrementP2Score();
            }

            String winnerText = "Player " + (winnerIndex + 1) + " Wins!";
            switchToGameOverScreen(winnerText, ScoreManager.getP1Score(), ScoreManager.getP2Score());
        }
    }

    /**
     * Switches to the game over screen and displays the scores.
     * @param winnerText the message indicating the winner
     * @param P1Score the score of player 1
     * @param P2Score the score of player 2
     */
    protected void switchToGameOverScreen(String winnerText, int P1Score, int P2Score) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game-over.fxml"));
            StackPane gameOverRoot = loader.load();

            GameOverController controller = loader.getController();
            controller.setWinnerText(winnerText);
            controller.setPlayersScore(P1Score, P2Score);

            Scene scene = new Scene(gameOverRoot);
            ((javafx.stage.Stage) mapGrid.getScene().getWindow()).setScene(scene);
        } catch (IOException e) {
        }
    }

    /**
     * Spawns a new power-up at the given grid coordinates.
     * @param row the row coordinate
     * @param col the column coordinate
     */
    public void spawnPowerUpAt(int row, int col) {
        // Decide type randomly
        PowerUp.Power[] possiblePowers = PowerUp.Power.values();
        PowerUp.Power randomPower = possiblePowers[new java.util.Random().nextInt(possiblePowers.length)];
        PowerUp newPowerUp = new PowerUp(row, col, randomPower, 3_000_000_000L/GameData.getGameSpeed());

        // Load appropriate image for the power-up type
        String imgPath;
        imgPath = switch (randomPower) {
            case SPEED ->      ImageLibrary.PowerSpeed;
            case BOMB_RANGE -> ImageLibrary.PowerRange;
            case EXTRA_BOMB -> ImageLibrary.PowerAmount;
            default ->         ImageLibrary.Power;
        };

        Image powerUpImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgPath)));

        StackPane powerUpNode = ResourceLoader.createPixelatedImageNode(powerUpImg, gameMap.getTileSize(), gameMap.getTileSize(), 0, 0);

        // Add power-up to tracking lists
        activePowerUps.add(newPowerUp);
        activePowerUpCells.add(powerUpNode);

        // Add to the grid
        mapGrid.add(powerUpNode, newPowerUp.getCol(), newPowerUp.getRow());
    }

    /**
     * Checks if the player has collided with any active power-up, and applies the effect if so.
     * @param player the player to check
     */
    private void checkPowerUpCollision(Player player) {
        if (activePowerUps.isEmpty()) return;

        for (int i = 0; i < activePowerUps.size(); i++) {
            PowerUp powerUp = activePowerUps.get(i);
            if (player.getRow() == powerUp.getRow() && player.getCol() == powerUp.getCol()) {
                // Remove power-up from the grid and lists
                SFXPlayer.play(SFXLibrary.POWER_UP);
                mapGrid.getChildren().remove(activePowerUpCells.get(i));
                activePowerUps.remove(i);
                activePowerUpCells.remove(i);

                player.setPower(powerUp.getPower(), System.nanoTime(), powerUp.getDuration(), bomb);

                break;
            }
        }
    }
}
