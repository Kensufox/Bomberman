package com.game.controllers;

import java.util.Objects;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.models.map.GameMap;
import com.game.utils.InputHandler;
import com.game.utils.ResourceLoader;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    private InputHandler inputHandler;

    private Player player1, player2;
    private StackPane player1Cell, player2Cell;

    private Bomb bomb;

    private GameMap gameMap;

    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();
            
        gameMap.setupMap(mapGrid);
            
        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg());
            
        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));
        player1Cell = ResourceLoader.createPixelatedImageNode(playerImg, gameMap.getTileSize(), gameMap.getTileSize());
        player2Cell = ResourceLoader.createPixelatedImageNode(playerImg, gameMap.getTileSize(), gameMap.getTileSize());

        player1 = new Player(1, 1);
        player2 = new Player(11, 13);
        mapGrid.add(player1Cell, player1.getCol(), player1.getRow());
        mapGrid.add(player2Cell, player2.getCol(), player2.getRow());

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPress);

    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode keyPressed = event.getCode();
        int dRow = 0, dCol = 0;

        // Custom keys (Player 1 & Player 2)
        if (keyPressed == inputHandler.getJ1Up()) {
            dRow = -1;
        } else if (keyPressed == inputHandler.getJ1Down()) {
            dRow = 1;
        } else if (keyPressed == inputHandler.getJ1Left()) {
            dCol = -1;
        } else if (keyPressed == inputHandler.getJ1Right()) {
            dCol = 1;
        } else if (keyPressed == inputHandler.getJ1Bomb()) {
            bomb.place(player1.getRow(), player1.getCol());
            return;
        } else if (keyPressed == inputHandler.getJ2Up()) {
            dRow = -1;
        } else if (keyPressed == inputHandler.getJ2Down()) {
            dRow = 1;
        } else if (keyPressed == inputHandler.getJ2Left()) {
            dCol = -1;
        } else if (keyPressed == inputHandler.getJ2Right()) {
            dCol = 1;
        } else if (keyPressed == inputHandler.getJ2Bomb()) {
            bomb.place(player2.getRow(), player2.getCol());
        }

        // No recognized movement
        else {
            return;
        }

        int newRow = player1.getRow() + dRow;
        int newCol = player1.getCol() + dCol;

        if (isWalkable(newRow, newCol)) {
            player1.move(dRow, dCol);
            // Move playerCell in the grid
            GridPane.setRowIndex(player1Cell, player1.getRow());
            GridPane.setColumnIndex(player1Cell, player1.getCol());
        }
    }

    private boolean isWalkable(int row, int col) {
        return gameMap.getMapData()[row][col] == '.';
    }

}