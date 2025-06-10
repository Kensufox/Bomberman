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
import com.game.models.entities.bot.BotPlayer;
import com.game.models.map.GameMap;
import com.game.utils.GameData;
import com.game.utils.ImageLibrary;
import com.game.utils.InputHandler;
import com.game.utils.ResourceLoader;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMapController {

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

    // Inner class to store info per player
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

        startMovementLoop();
    }

    protected Player[] createPlayers() {
        Player player1 = new Player(1, 1, Player.State.ALIVE);
        Player player2 = new Player(11, 13, Player.State.ALIVE);
        return new Player[] { player1, player2 };
    }

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
            }
        };

        movementLoop.start();
    }

    protected void movePlayerIfPossible(Player player, StackPane cell, int dRow, int dCol) {
        int oldRow = player.getRow();
        int oldCol = player.getCol();
        int newRow = oldRow + dRow;
        int newCol = oldCol + dCol;

        if (isWalkable(newRow, newCol)) {
            player.move(dRow, dCol);

            GridPane.setRowIndex(cell, player.getRow());
            GridPane.setColumnIndex(cell, player.getCol());
            checkPowerUpCollision(player);
        }
        cell.toFront();
    }

    protected boolean isWalkable(int row, int col) {
        if (row < 0 || col < 0 || row >= gameMap.getMapData().length || col >= gameMap.getMapData()[0].length) {
            return false; // safety out of bounds
        }
        char cell = gameMap.getMapData()[row][col];
        return cell == '.' || cell == 'P';
    }

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
            Player winner = alivePlayers.get(0).player;
            winner.setScore(winner.getScore() + 1);

            String winnerText = "Player " + (players.indexOf(alivePlayers.get(0)) + 1) + " Wins!";
            switchToGameOverScreen(winnerText,
                    players.get(0).player.getScore(),
                    players.get(1).player.getScore()); // Adjust according to your number of players
        }
    }

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

    public void spawnPowerUpAt(int row, int col) {
        // Decide type randomly or fixed for now
        PowerUp.Power[] possiblePowers = PowerUp.Power.values();
        PowerUp.Power randomPower = possiblePowers[new java.util.Random().nextInt(possiblePowers.length)];

        // Create the PowerUp object (adjust duration and position)
        PowerUp newPowerUp = new PowerUp(row, col, randomPower, 3_000_000_000L/GameData.gameSpeed);

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

    private void checkPowerUpCollision(Player player) {
        if (activePowerUps.isEmpty()) return;

        for (int i = 0; i < activePowerUps.size(); i++) {
            PowerUp powerUp = activePowerUps.get(i);
            if (player.getRow() == powerUp.getRow() && player.getCol() == powerUp.getCol()) {
                // Remove power-up from the grid and lists
                mapGrid.getChildren().remove(activePowerUpCells.get(i));
                activePowerUps.remove(i);
                activePowerUpCells.remove(i);

                player.setPower(powerUp.getPower(), System.nanoTime(), powerUp.getDuration(), bomb);

                break;
            }
        }
    }
}
