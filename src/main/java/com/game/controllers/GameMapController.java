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

    private Player player;
    private StackPane playerCell;

    private Bomb bomb;

    private GameMap gameMap;

    public void initialize() {
        this.inputHandler = new InputHandler();
        this.gameMap = new GameMap();
            
        gameMap.setupMap(mapGrid);
            
        this.bomb = new Bomb(mapGrid, gameMap.getMapData(), gameMap.getTiles(), gameMap.getEmptyImg());
            
        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));
        playerCell = ResourceLoader.createPixelatedImageNode(playerImg, gameMap.getTileSize(), gameMap.getTileSize());
            
        player = new Player(1, 1);
        mapGrid.add(playerCell, player.getCol(), player.getRow());
            
        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPress);

    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode keyPressed = event.getCode();
        int dRow = 0, dCol = 0;

        // Touches personnalisées (Joueur 1)
        if (keyPressed == inputHandler.getJ1Up()) {
            dRow = -1;
        } else if (keyPressed == inputHandler.getJ1Down()) {
            dRow = 1;
        } else if (keyPressed == inputHandler.getJ1Left()) {
            dCol = -1;
        } else if (keyPressed == inputHandler.getJ1Right()) {
            dCol = 1;
        } else if (keyPressed == inputHandler.getJ1Bomb()) {
            bomb.place(player.getRow(), player.getCol());
            return;
        }

        // Aucun mouvement reconnu
        else {
            return;
        }

        int newRow = player.getRow() + dRow;
        int newCol = player.getCol() + dCol;

        if (isWalkable(newRow, newCol)) {
            player.move(dRow, dCol);
            // Move playerCell in the grid
            GridPane.setRowIndex(playerCell, player.getRow());
            GridPane.setColumnIndex(playerCell, player.getCol());
        }
    }

    private boolean isWalkable(int row, int col) {
        return gameMap.getMapData()[row][col] == '.';
    }

}