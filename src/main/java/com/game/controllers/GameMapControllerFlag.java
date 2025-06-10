package com.game.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.entities.PowerUp;
import com.game.models.map.GameMap;
import com.game.utils.GameData;
import com.game.utils.ImageLibrary;
import com.game.utils.InputHandler;
import com.game.utils.ResourceLoader;
import com.game.utils.SFXLibrary;
import com.game.utils.SFXPlayer;
import com.game.utils.ScoreManager;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMapControllerFlag extends GameMapController {

    @FXML
    protected GridPane mapGrid;

    @FXML
    protected GridPane backgroundGrid;

    protected InputHandler inputHandler;
    protected PowerUp powerUp;
    protected StackPane powerUpCell;
    protected Bomb bomb;
    protected GameMap gameMap;

    protected final Set<KeyCode> pressedKeys = new HashSet<>();

    // Generic list of players and their contexts (cells + controls)
    protected final List<PlayerContext> players = new ArrayList<>();

    private final List<PowerUp> activePowerUps = new ArrayList<>();
    private final List<StackPane> activePowerUpCells = new ArrayList<>();

    // Capture the Flag specific variables
    private Flag player1Flag;
    private Flag player2Flag;
    private StackPane player1FlagCell;
    private StackPane player2FlagCell;

    // Inner class to store info per player
    protected static class PlayerContext {
        final Player player;
        final StackPane cell;
        final InputHandler.PlayerControls controls;
        final int spawnRow;
        final int spawnCol;
        boolean hasOpponentFlag;

        PlayerContext(Player player, StackPane cell, InputHandler.PlayerControls controls, int spawnRow, int spawnCol) {
            this.player = player;
            this.cell = cell;
            this.controls = controls;
            this.spawnRow = spawnRow;
            this.spawnCol = spawnCol;
            this.hasOpponentFlag = false;
        }
    }

    // Inner class to represent a flag
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
        public int getRow() { return row; }
        public int getCol() { return col; }
        public int getHomeRow() { return homeRow; }
        public int getHomeCol() { return homeCol; }
        public boolean isAtHome() { return isAtHome; }
        public boolean isCarried() { return isCarried; }
        public Player getCarrier() { return carrier; }

        public void setPosition(int row, int col) {
            this.row = row;
            this.col = col;
            this.isAtHome = (row == homeRow && col == homeCol);
        }

        public void pickUp(Player player) {
            this.isCarried = true;
            this.carrier = player;
            this.isAtHome = false;
        }

        public void drop(int row, int col) {
            this.isCarried = false;
            this.carrier = null;
            setPosition(row, col);
        }

        public void returnHome() {
            this.isCarried = false;
            this.carrier = null;
            setPosition(homeRow, homeCol);
        }
    }

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

    private void setupFlags() {
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
     * @return Player[]
     */
    protected Player[] createPlayers() {
        Player player1 = new Player(1, 1, Player.State.ALIVE);
        Player player2 = new Player(11, 13, Player.State.ALIVE);
        return new Player[] { player1, player2 };
    }

    /** 
     * @param event
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
     * @param event
     */
    protected void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

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
     * @param player
     * @param cell
     * @param dRow
     * @param dCol
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
            
            // Check for flag interactions
            checkFlagInteraction(player);
            
            // Check for power-up collisions
            checkPowerUpCollision(player);
        }
        cell.toFront();
    }

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
     * @param row
     * @param col
     * @return boolean
     */
    protected boolean isWalkable(int row, int col) {
        if (row < 0 || col < 0 || row >= gameMap.getMapData().length || col >= gameMap.getMapData()[0].length) {
            return false; // safety out of bounds
        }
        char cell = gameMap.getMapData()[row][col];
        return cell == '.' || cell == 'P';
    }

    /** 
     * @param player
     */
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
     * @param winnerText
     * @param P1Score
     * @param P2Score
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
     * @param row
     * @param col
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
     * @param player
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