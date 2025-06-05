package com.game.controllers;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.entities.PowerUp;
import com.game.models.map.GameMap;
import com.game.utils.InputHandler;

import com.game.utils.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    private InputHandler inputHandler;

    private Player player1, player2;
    private StackPane player1Cell, player2Cell;

    private PowerUp powerUp;
    private StackPane powerUpCell;

    private Bomb bomb;

    private GameMap gameMap;

    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();

        gameMap.setupMap(mapGrid);

        //inputHandler.printConfiguration();

        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg());

        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));
        player1Cell = ResourceLoader.createPixelatedImageNode(playerImg, gameMap.getTileSize(), gameMap.getTileSize());
        player2Cell = ResourceLoader.createPixelatedImageNode(playerImg, gameMap.getTileSize(), gameMap.getTileSize());
        player1Cell.toFront();
        player2Cell.toFront();

        player1 = new Player(1, 1);
        player2 = new Player(11, 13);
        mapGrid.add(player1Cell, player1.getCol(), player1.getRow());
        mapGrid.add(player2Cell, player2.getCol(), player2.getRow());

        Image powerUpImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/power-up.png"))); 
        powerUpCell = ResourceLoader.createPixelatedImageNode(powerUpImg, gameMap.getTileSize(), gameMap.getTileSize());
        powerUp = new PowerUp(2, 1);
        mapGrid.add(powerUpCell, powerUp.getCol(), powerUp.getRow());

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPress);

    }

    private void checkPowerUpCollision(Player player, StackPane playerCell) {
        if (powerUp == null) return;

        if (player.getRow() == powerUp.getRow() && player.getCol() == powerUp.getCol()) {
            mapGrid.getChildren().remove(powerUpCell);  // Supprime le power-up visuellement
            System.out.println("Player " + (player == player1 ? "1" : "2") +
                " picked up the power-up at (" + powerUp.getRow() + ", " + powerUp.getCol() + ")");
            powerUp = null;  // Le power-up n’existe plus
        }
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode keyPressed = event.getCode();
        int dRowJ1 = 0, dColJ1 = 0;
        int dRowJ2 = 0, dColJ2 = 0;

        // Custom keys (Player 1 & Player 2)
        if (keyPressed == inputHandler.getJ1Up()) {
            dRowJ1 = -1;
        } else if (keyPressed == inputHandler.getJ1Down()) {
            dRowJ1 = 1;
        } else if (keyPressed == inputHandler.getJ1Left()) {
            dColJ1 = -1;
        } else if (keyPressed == inputHandler.getJ1Right()) {
            dColJ1 = 1;
        } else if (keyPressed == inputHandler.getJ1Bomb()) {
            bomb.place(player1.getRow(), player1.getCol());
        } else if (keyPressed == inputHandler.getJ2Up()) {
            dRowJ2 = -1;
        } else if (keyPressed == inputHandler.getJ2Down()) {
            dRowJ2 = 1;
        } else if (keyPressed == inputHandler.getJ2Left()) {
            dColJ2 = -1;
        } else if (keyPressed == inputHandler.getJ2Right()) {
            dColJ2 = 1;
        } else if (keyPressed == inputHandler.getJ2Bomb()) {
            bomb.place(player2.getRow(), player2.getCol());
        }

        int newRowJ1 = player1.getRow() + dRowJ1;
        int newColJ1 = player1.getCol() + dColJ1;
        int newRowJ2 = player2.getRow() + dRowJ2;
        int newColJ2 = player2.getCol() + dColJ2;

        if (isWalkable(newRowJ1, newColJ1)) {
            player1.move(dRowJ1, dColJ1);
            GridPane.setRowIndex(player1Cell, player1.getRow());
            GridPane.setColumnIndex(player1Cell, player1.getCol());
            checkPowerUpCollision(player1, player1Cell);
        }
        
        if (isWalkable(newRowJ2, newColJ2)) {
            player2.move(dRowJ2, dColJ2);
            GridPane.setRowIndex(player2Cell, player2.getRow());
            GridPane.setColumnIndex(player2Cell, player2.getCol());
            checkPowerUpCollision(player2, player2Cell);
        }

        player1Cell.toFront();
        player2Cell.toFront();
    }

    private boolean isWalkable(int row, int col) {
        return gameMap.getMapData()[row][col] == '.';
    }

}