package com.game.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.entities.PowerUp;
import com.game.models.map.GameMap;
import com.game.utils.InputHandler;
import com.game.utils.ResourceLoader;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    @FXML
    private GridPane backgroundGrid;
    
    private InputHandler inputHandler;
    private Player player1, player2;
    private StackPane player1Cell, player2Cell;
    private PowerUp powerUp;
    private StackPane powerUpCell;
    private Bomb bomb;
    private GameMap gameMap;

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();
        gameMap.setupBackground(gameMap, backgroundGrid);
        gameMap.setupMap(mapGrid);

        Image player1Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player1.png")));
        Image player2Img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player2.png")));
        player1Cell = ResourceLoader.createPixelatedImageNode(player1Img, gameMap.getTileSize(), (gameMap.getTileSize() * 1.75), 0, 15);
        player2Cell = ResourceLoader.createPixelatedImageNode(player2Img, gameMap.getTileSize(), (gameMap.getTileSize() * 1.75), 0, 15);
        player1Cell.toFront();
        player2Cell.toFront();

        player1 = new Player(1, 1, Player.State.ALIVE);
        player2 = new Player(11, 13, Player.State.ALIVE);
        mapGrid.add(player1Cell, player1.getCol(), player1.getRow());
        gameMap.getMapData()[player1.getRow()][player1.getCol()] = '1';
        mapGrid.add(player2Cell, player2.getCol(), player2.getRow());
        gameMap.getMapData()[player2.getRow()][player2.getCol()] = '2';

        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg(), player1, player2, this);

        Image powerUpImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/power-up.png")));
        powerUpCell = ResourceLoader.createPixelatedImageNode(powerUpImg, gameMap.getTileSize(), gameMap.getTileSize(), 0, 0);
        powerUp = new PowerUp(2, 1, PowerUp.Power.SPEED, 3_000_000_000L);
        mapGrid.add(powerUpCell, powerUp.getCol(), powerUp.getRow());

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPressed);
        mapGrid.setOnKeyReleased(this::handleKeyReleased);

        startMovementLoop();
    }

    private void handleKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode());
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void startMovementLoop() {
        AnimationTimer movementLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (player1.getPower() != null && now >= player1.getPowerEndTime()) {
                    player1.removePower();
                }

                if (player2.getPower() != null && now >= player2.getPowerEndTime()) {
                    player2.removePower();
                }

                int dRowJ1 = 0, dColJ1 = 0;
                int dRowJ2 = 0, dColJ2 = 0;

                if (player1.getState() == Player.State.DEAD) return;
                else if (pressedKeys.contains(inputHandler.getJ1Up())) dRowJ1 = -1;
                else if (pressedKeys.contains(inputHandler.getJ1Down())) dRowJ1 = 1;
                else if (pressedKeys.contains(inputHandler.getJ1Left())) dColJ1 = -1;
                else if (pressedKeys.contains(inputHandler.getJ1Right())) dColJ1 = 1;

                if (player2.getState() == Player.State.DEAD) return;
                else if (pressedKeys.contains(inputHandler.getJ2Up())) dRowJ2 = -1;
                else if (pressedKeys.contains(inputHandler.getJ2Down())) dRowJ2 = 1;
                else if (pressedKeys.contains(inputHandler.getJ2Left())) dColJ2 = -1;
                else if (pressedKeys.contains(inputHandler.getJ2Right())) dColJ2 = 1;

                if (pressedKeys.contains(inputHandler.getJ1Bomb()))
                    bomb.place(player1.getRow(), player1.getCol());
                if (pressedKeys.contains(inputHandler.getJ2Bomb()))
                    bomb.place(player2.getRow(), player2.getCol());

                if ((dRowJ1 != 0 || dColJ1 != 0) && player1.canMove(now)) {
                    movePlayerIfPossible(player1, player1Cell, dRowJ1, dColJ1);
                    player1.updateLastMoveTime(now);
                }

                if ((dRowJ2 != 0 || dColJ2 != 0) && player2.canMove(now)) {
                    movePlayerIfPossible(player2, player2Cell, dRowJ2, dColJ2);
                    player2.updateLastMoveTime(now);
                }
            }
        };

        movementLoop.start();
    }

    private void movePlayerIfPossible(Player player, StackPane cell, int dRow, int dCol) {

        if (dRow == 0 && dCol == 0) return;

        int oldRow = player.getRow();
        int oldCol = player.getCol();
        int newRow = oldRow + dRow;
        int newCol = oldCol + dCol;

        if (gameMap.getMapData()[newRow][newCol] == 'X') return;

        if (isWalkable(newRow, newCol)) {
            // Clear previous player position in mapData
            gameMap.getMapData()[oldRow][oldCol] = '.';
            player.move(dRow, dCol);
            gameMap.getMapData()[player.getRow()][player.getCol()] = player == player1 ? '1' : '2';

            // Visually move the player
            GridPane.setRowIndex(cell, player.getRow());
            GridPane.setColumnIndex(cell, player.getCol());
            checkPowerUpCollision(player);
        }
        cell.toFront();
    }

    private boolean isWalkable(int row, int col) {
        char cell = gameMap.getMapData()[row][col];
        return cell == '.' || cell == 'P';
    }

    private void checkPowerUpCollision(Player player) {
        if (powerUp == null) return;

        if (player.getRow() == powerUp.getRow() && player.getCol() == powerUp.getCol()) {
            mapGrid.getChildren().remove(powerUpCell);
            System.out.println("Player " + (player == player1 ? "1" : "2") +
                    " picked up the power-up at (" + powerUp.getRow() + ", " + powerUp.getCol() + ")");
            //player.setPower(powerUp.getPower());
            player.setPower(powerUp.getPower(), System.nanoTime(), powerUp.getDuration());
            player.appliPower();
            powerUp = null;
        }
    }

    public void killPlayer(Player player) {
        if (player == player1 && player1.getState() == Player.State.ALIVE) {
            player1.setState(Player.State.DEAD);
            mapGrid.getChildren().remove(player1Cell);
            System.out.println("Player 1 died");
            switchToGameOverScreen("Player 2 Wins!");
        } else if (player == player2 && player2.getState() == Player.State.ALIVE) {
            player2.setState(Player.State.DEAD);
            mapGrid.getChildren().remove(player2Cell);
            System.out.println("Player 2 died");
            switchToGameOverScreen("Player 1 Wins!");
        }
    }

    private void switchToGameOverScreen(String winnerText) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent gameOverRoot = loader.load();

            // Optionally pass winner text to controller
            GameOverController controller = loader.getController();
            controller.setWinnerText(winnerText);

            Scene scene = new Scene(gameOverRoot);
            mapGrid.getScene().setRoot(gameOverRoot); // Replaces the current root
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
