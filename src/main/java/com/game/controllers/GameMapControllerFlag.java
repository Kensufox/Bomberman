/**
 * GameMapControllerFlag extends GameMapController to implement Capture the Flag mechanics.
 * It sets up the map, players, flags, power-ups, and handles movement, collisions,
 * flag capture logic, and transition to the game over screen.
 */

package com.game.controllers;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.entities.PowerUp;
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
 * GameMapControllerFlag extends GameMapController to implement Capture the Flag mechanics.
 *
 * This class handles the setup and management of a Capture the Flag game mode,
 * including player initialization, flag placement, player movements, flag interactions,
 * power-ups, bombs, and transitioning to the game over screen when a player wins.
 *
 * Key features:
 * - Manages two players with their own flags at spawn points.
 * - Tracks when flags are picked up, dropped, or returned to base.
 * - Supports player movement with keyboard input.
 * - Checks for flag captures to determine game victory.
 * - Handles power-ups spawning and collection.
 * - Supports bomb placement and player elimination.
 *
 * It utilizes JavaFX's GridPane for the map display and AnimationTimer for smooth movement.
 */
public class GameMapControllerFlag extends GameMapController {

    /**
     * Default constructor for GameMapControllerFlag.
     * Initializes the controller for Capture the Flag mode.
     * Typically, the 'initialize()' method will be called by the JavaFX framework
     * after the constructor to setup the game elements and start the game.
     */
    public GameMapControllerFlag() {
        super();
        // No explicit initialization here; defer setup to initialize()
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
    private final List<PowerUp> activePowerUps = new ArrayList<>();

    /** Visual nodes representing active power-ups */
    private final List<StackPane> activePowerUpCells = new ArrayList<>();

    /** Capture the Flag specific variables */
    private Flag player1Flag;
    private Flag player2Flag;
    private StackPane player1FlagCell;
    private StackPane player2FlagCell;

    /** Inner class for storing a player, their cell, and controls */
    protected static class PlayerContext {
        final Player player;
        final StackPane cell;
        final InputHandler.PlayerControls controls;
        final int spawnRow;
        final int spawnCol;
        boolean hasOpponentFlag;

        /**
         * Inner class representing a player's context within the game.
         * Includes the Player instance, graphical cell, controls, spawn position,
         * and whether the player is currently carrying the opponent's flag.
         */
        PlayerContext(Player player, StackPane cell, InputHandler.PlayerControls controls, int spawnRow, int spawnCol) {
            this.player = player;
            this.cell = cell;
            this.controls = controls;
            this.spawnRow = spawnRow;
            this.spawnCol = spawnCol;
            this.hasOpponentFlag = false;
        }
    }

    /**
     * Inner class representing a flag in Capture the Flag mode.
     * Stores its position, home base, carrier state, and logic for being picked up, dropped, or returned.
     */

    protected static class Flag {
        private int row;
        private int col;
        private final int homeRow;
        private final int homeCol;
        private boolean isAtHome;
        private boolean isCarried;
        private Player carrier;

        Flag(int homeRow, int homeCol) {
            this.homeRow = homeRow;
            this.homeCol = homeCol;
            this.row = homeRow;
            this.col = homeCol;
            this.isAtHome = true;
            this.isCarried = false;
            this.carrier = null;
        }

// Getters and setters
        /**
         * @return the current row of the flag
         */
        public int getRow() { return row; }
        /**
         * @return the current column of the flag
         */
        public int getCol() { return col; }
        /**
         * @return the home row of the flag
         */
        public int getHomeRow() { return homeRow; }
        /**
         * @return the home column of the flag
         */
        public int getHomeCol() { return homeCol; }
        /**
         * @return true if the flag is at its home position
         */
        public boolean isAtHome() { return isAtHome; }
        /**
         * @return true if the flag is currently being carried
         */
        public boolean isCarried() { return isCarried; }
        /**
         * @return the player currently carrying the flag, or null if not carried
         */
        public Player getCarrier() { return carrier; }

        /**
         * Sets the flag's position and updates whether it is at its home base.
         *
         * @param row the row to set
         * @param col the column to set
         */
        public void setPosition(int row, int col) {
            this.row = row;
            this.col = col;
            this.isAtHome = (row == homeRow && col == homeCol);
        }

        /**
         * Assigns a player as the carrier of the flag and marks it as taken.
         *
         * @param player the player picking up the flag
         */
        public void pickUp(Player player) {
            this.isCarried = true;
            this.carrier = player;
            this.isAtHome = false;
        }

        /**
         * Drops the flag at the specified position.
         *
         * @param row the row where the flag is dropped
         * @param col the column where the flag is dropped
         */
        public void drop(int row, int col) {
            this.isCarried = false;
            this.carrier = null;
            setPosition(row, col);
        }


        /**
         * Returns the flag to its home position and clears the carrier.
         */
        public void returnHome() {
            this.isCarried = false;
            this.carrier = null;
            setPosition(homeRow, homeCol);
        }
    }

    /**
     * Initializes the game map and players, sets up input handling,
     * flags, bombs, and starts the movement loop.
     */
    @Override
    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();
        gameMap.setupBackground(gameMap, backgroundGrid);
        gameMap.setupMap(mapGrid);

        // Create player images
        Image player1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Player1)));
        Image player2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Player2)));

        // Create players
        Player[] players_temps = createPlayers();
        Player player1 = players_temps[0];
        Player player2 = players_temps[1];

        // Create graphical nodes
        StackPane player1Cell = ResourceLoader.createPixelatedImageNode(player1Img, gameMap.getTileSize(), gameMap.getTileSize() * 1.75, 0, 15);
        StackPane player2Cell = ResourceLoader.createPixelatedImageNode(player2Img, gameMap.getTileSize(), gameMap.getTileSize() * 1.75, 0, 15);

        // Add players to the list with their controls and spawn points
        players.add(new PlayerContext(player1, player1Cell, inputHandler.getJ1Controls(), 1, 1));
        players.add(new PlayerContext(player2, player2Cell, inputHandler.getJ2Controls(), 11, 13));

        // Add players to the grid
        for (PlayerContext ctx : players) {
            mapGrid.add(ctx.cell, ctx.player.getCol(), ctx.player.getRow());
            ctx.cell.toFront();
        }

        // Initialize flags at player spawn points
        setupFlags();

        // Initialize bomb
        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg(), 
            players.stream().map(pc -> pc.player).collect(Collectors.toList()), this);

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPressed);
        mapGrid.setOnKeyReleased(this::handleKeyReleased);

        startMovementLoop();
    }

    /**
     * Sets up the flags for both players at their respective spawn points.
     */
    void setupFlags() {
        // Create flags at each player's spawn point
        player1Flag = new Flag(1, 1); // Player 1's spawn
        player2Flag = new Flag(11, 13); // Player 2's spawn

        // Create flag images (you might want to use different images for each team)
        Image flag1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Flag1))); // Replace with actual flag image
        Image flag2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Flag2))); // Replace with actual flag image

        // Create flag visual nodes
        player1FlagCell = ResourceLoader.createPixelatedImageNode(flag1Img, gameMap.getTileSize(), gameMap.getTileSize(), 0, 0);
        player2FlagCell = ResourceLoader.createPixelatedImageNode(flag2Img, gameMap.getTileSize(), gameMap.getTileSize(), 0, 0);

        // Add flags to the grid
        mapGrid.add(player1FlagCell, player1Flag.getCol(), player1Flag.getRow());
        mapGrid.add(player2FlagCell, player2Flag.getCol(), player2Flag.getRow());
    }

    /**
     * Creates and returns the players for Capture the Flag.
     *
     * @return an array of Player objects representing both players
     */
    protected Player[] createPlayers() {
        Player player1 = new Player(1, 1, Player.State.ALIVE);
        Player player2 = new Player(11, 13, Player.State.ALIVE);
        return new Player[] { player1, player2 };
    }

    /**
     * Handles key press events for player movement and bomb placement.
     *
     * @param event KeyEvent representing the pressed key
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
     * Handles key release events and removes keys from the pressed set.
     *
     * @param event KeyEvent representing the released key
     */
    protected void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    /**
     * Starts the animation loop to continuously update player movement
     * and flag tracking based on input.
     */
    @Override
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

                // Update flag positions for carried flags
                updateCarriedFlags();
            }
        };

        movementLoop.start();
    }

    /**
     * Updates the positions of any carried flags to match the carrier's location.
     */
    private void updateCarriedFlags() {
        // Update player 1 flag position if carried
        if (player1Flag.isCarried() && player1Flag.getCarrier() != null) {
            Player carrier = player1Flag.getCarrier();
            GridPane.setRowIndex(player1FlagCell, carrier.getRow());
            GridPane.setColumnIndex(player1FlagCell, carrier.getCol());
            player1Flag.setPosition(carrier.getRow(), carrier.getCol());
        }

        // Update player 2 flag position if carried
        if (player2Flag.isCarried() && player2Flag.getCarrier() != null) {
            Player carrier = player2Flag.getCarrier();
            GridPane.setRowIndex(player2FlagCell, carrier.getRow());
            GridPane.setColumnIndex(player2FlagCell, carrier.getCol());
            player2Flag.setPosition(carrier.getRow(), carrier.getCol());
        }
    }

    /**
     * Attempts to move the player in the given direction if the destination is walkable.
     * Also checks for flag and power-up interactions.
     *
     * @param player the player to move
     * @param cell the StackPane associated with the player
     * @param dRow the row direction delta
     * @param dCol the column direction delta
     */
    @Override
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
            
            // Check for flag interactions
            checkFlagInteraction(player);
            
            // Check for power-up collisions
            checkPowerUpCollision(player);
        }
        cell.toFront();
    }

    /**
     * Checks if a player can interact with flags (pickup or score).
     *
     * @param player the player to check for flag interaction
     */
    private void checkFlagInteraction(Player player) {
        PlayerContext playerCtx = players.stream()
            .filter(p -> p.player == player)
            .findFirst()
            .orElse(null);
            
        if (playerCtx == null) return;

        int playerIndex = players.indexOf(playerCtx);
        
        // Player 1 (index 0) can steal Player 2's flag
        if (playerIndex == 0) {
            // Check if player 1 is at player 2's flag position and flag is not carried
            if (player.getRow() == player2Flag.getRow() && 
                player.getCol() == player2Flag.getCol() && 
                !player2Flag.isCarried()) {
                
                // Pick up the opponent's flag
                player2Flag.pickUp(player);
                playerCtx.hasOpponentFlag = true;
                SFXPlayer.play(SFXLibrary.POWER_UP); // Play pickup sound
            }
            // Check if player 1 is at their spawn with opponent's flag (win condition)
            else if (player.getRow() == playerCtx.spawnRow && 
                     player.getCol() == playerCtx.spawnCol && 
                     playerCtx.hasOpponentFlag) {
                
                // Player 1 wins!
                ScoreManager.incrementP1Score();
                switchToGameOverScreen("Player 1 Captures the Flag!", 
                    ScoreManager.getP1Score(), ScoreManager.getP2Score());
            }
        }
        // Player 2 (index 1) can steal Player 1's flag
        else if (playerIndex == 1) {
            // Check if player 2 is at player 1's flag position and flag is not carried
            if (player.getRow() == player1Flag.getRow() && 
                player.getCol() == player1Flag.getCol() && 
                !player1Flag.isCarried()) {
                
                // Pick up the opponent's flag
                player1Flag.pickUp(player);
                playerCtx.hasOpponentFlag = true;
                SFXPlayer.play(SFXLibrary.POWER_UP); // Play pickup sound
            }
            // Check if player 2 is at their spawn with opponent's flag (win condition)
            else if (player.getRow() == playerCtx.spawnRow && 
                     player.getCol() == playerCtx.spawnCol && 
                     playerCtx.hasOpponentFlag) {
                
                // Player 2 wins!
                ScoreManager.incrementP2Score();
                switchToGameOverScreen("Player 2 Captures the Flag!", 
                    ScoreManager.getP1Score(), ScoreManager.getP2Score());
            }
        }
    }

    /**
     * Checks if the tile at the given position is walkable.
     *
     * @param row row index
     * @param col column index
     * @return true if walkable, false otherwise
     */
    protected boolean isWalkable(int row, int col) {
        if (row < 0 || col < 0 || row >= gameMap.getMapData().length || col >= gameMap.getMapData()[0].length) {
            return false; // safety out of bounds
        }
        char cell = gameMap.getMapData()[row][col];
        return cell == '.' || cell == 'P';
    }

    /**
     * Kills the given player, removes them from the map, and returns any carried flag.
     * Checks if only one player remains to determine winner.
     *
     * @param player the player to eliminate
     */
    @Override
    public void killPlayer(Player player) {
        if (player.getState() == Player.State.DEAD) return;

        player.setState(Player.State.DEAD);
        PlayerContext deadCtx = players.stream().filter(p -> p.player == player).findFirst().orElse(null);
        if (deadCtx != null) {
            mapGrid.getChildren().remove(deadCtx.cell);
            
            // If the dead player was carrying a flag, drop it and return it home
            if (deadCtx.hasOpponentFlag) {
                deadCtx.hasOpponentFlag = false;
                
                // Determine which flag to return based on player index
                int playerIndex = players.indexOf(deadCtx);
                if (playerIndex == 0 && player2Flag.isCarried()) {
                    // Player 1 died while carrying player 2's flag
                    player2Flag.returnHome();
                    GridPane.setRowIndex(player2FlagCell, player2Flag.getRow());
                    GridPane.setColumnIndex(player2FlagCell, player2Flag.getCol());
                } else if (playerIndex == 1 && player1Flag.isCarried()) {
                    // Player 2 died while carrying player 1's flag
                    player1Flag.returnHome();
                    GridPane.setRowIndex(player1FlagCell, player1Flag.getRow());
                    GridPane.setColumnIndex(player1FlagCell, player1Flag.getCol());
                }
            }
        }

        // Check if there is only one player alive to declare the winner
        List<PlayerContext> alivePlayers = players.stream()
                .filter(p -> p.player.getState() == Player.State.ALIVE)
                .collect(Collectors.toList());

        if (alivePlayers.size() == 1) {
            // In CTF mode, surviving is also a way to win
            int winnerIndex = players.indexOf(alivePlayers.get(0));
            if (winnerIndex == 0) {
                ScoreManager.incrementP1Score();
            } else if (winnerIndex == 1) {
                ScoreManager.incrementP2Score();
            }

            String winnerText = "Player " + (winnerIndex + 1) + " Wins by Elimination!";
            switchToGameOverScreen(winnerText, ScoreManager.getP1Score(), ScoreManager.getP2Score());
        }
    }

    /**
     * Switches the scene to the Game Over screen and displays the result.
     *
     * @param winnerText the message to show on the game over screen
     * @param P1Score Player 1's score
     * @param P2Score Player 2's score
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
            e.printStackTrace();
        }
    }

    /**
     * Spawns a power-up at the given map location with a random type.
     *
     * @param row the row to spawn the power-up
     * @param col the column to spawn the power-up
     */
    public void spawnPowerUpAt(int row, int col) {
        // Decide type randomly or fixed for now
        PowerUp.Power[] possiblePowers = PowerUp.Power.values();
        PowerUp.Power randomPower = possiblePowers[new java.util.Random().nextInt(possiblePowers.length)];

        // Create the PowerUp object (adjust duration and position)
        PowerUp newPowerUp = new PowerUp(row, col, randomPower, 3_000_000_000L/GameData.getGameSpeed());

        // Load appropriate image for the power-up type, e.g.:
        String imgPath;
        imgPath = switch (randomPower) {
            case SPEED ->      ImageLibrary.PowerSpeed;
            case BOMB_RANGE -> ImageLibrary.PowerRange;
            case EXTRA_BOMB -> ImageLibrary.PowerAmount;
            default ->         ImageLibrary.Power;
        }; // add other cases here

        Image powerUpImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgPath)));

        StackPane powerUpNode = ResourceLoader.createPixelatedImageNode(powerUpImg, gameMap.getTileSize(), gameMap.getTileSize(), 0, 0);

        // Add power-up to your tracking lists
        activePowerUps.add(newPowerUp);
        activePowerUpCells.add(powerUpNode);

        // Add to the grid
        mapGrid.add(powerUpNode, newPowerUp.getCol(), newPowerUp.getRow());
    }

    /**
     * Checks if a player has collided with a power-up and applies its effects.
     *
     * @param player the player to check for collision
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

    /**
     * Gets the flag for player 1.
     *
     * @return the player 1 flag
     */
    public Flag getPlayer1Flag() {
        return player1Flag;
    }

    /**
     * Sets the flag for player 1.
     *
     * @param player1Flag the flag to set for player 1
     */
    public void setPlayer1Flag(Flag player1Flag) {
        this.player1Flag = player1Flag;
    }

    /**
     * Gets the flag for player 2.
     *
     * @return the player 2 flag
     */
    public Flag getPlayer2Flag() {
        return player2Flag;
    }

    /**
     * Sets the flag for player 2.
     *
     * @param player2Flag the flag to set for player 2
     */
    public void setPlayer2Flag(Flag player2Flag) {
        this.player2Flag = player2Flag;
    }

    /**
     * Gets the cell (StackPane) containing the flag for player 1.
     *
     * @return the StackPane representing player 1's flag cell
     */
    public StackPane getPlayer1FlagCell() {
        return player1FlagCell;
    }

    /**
     * Sets the cell (StackPane) containing the flag for player 1.
     *
     * @param player1FlagCell the StackPane to set for player 1's flag cell
     */
    public void setPlayer1FlagCell(StackPane player1FlagCell) {
        this.player1FlagCell = player1FlagCell;
    }

    /**
     * Gets the cell (StackPane) containing the flag for player 2.
     *
     * @return the StackPane representing player 2's flag cell
     */
    public StackPane getPlayer2FlagCell() {
        return player2FlagCell;
    }

    /**
     * Sets the cell (StackPane) containing the flag for player 2.
     *
     * @param player2FlagCell the StackPane to set for player 2's flag cell
     */
    public void setPlayer2FlagCell(StackPane player2FlagCell) {
        this.player2FlagCell = player2FlagCell;
    }
}