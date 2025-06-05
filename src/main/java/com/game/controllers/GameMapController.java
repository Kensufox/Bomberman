package com.game.controllers;

import java.util.Objects;
import java.util.Random;

import com.game.models.entities.Bomb;
import com.game.models.entities.Player;
import com.game.utils.ResourceLoader;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    private final int TILE_SIZE = 40;
    private final int ROWS = 13;
    private final int COLS = 15;

    private final char[][] mapData = new char[ROWS][COLS]; // W = wall, B = breakable, . = empty
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];

    private Player player;
    private StackPane playerCell;

    private Bomb bomb;

    private Image wallImg;
    private Image breakableImg;
    private Image emptyImg;

    public void initialize() {
        // Load tile textures
        wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/inf_wall.png")));
        breakableImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/weak_wall.png")));
        emptyImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/empty.png")));

        setupGrid();
        generateMap();

        bomb = new Bomb(mapGrid, mapData, tiles, emptyImg);

        // Load player image and create pixelated canvas icon
        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));
        playerCell = ResourceLoader.createPixelatedImageNode(playerImg, TILE_SIZE, TILE_SIZE);

        player = new Player(1, 1);
        mapGrid.add(playerCell, player.getCol(), player.getRow());

        mapGrid.setFocusTraversable(true);
        mapGrid.setOnKeyPressed(this::handleKeyPress);
    }

    private void setupGrid() {
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);
        mapGrid.setStyle("-fx-padding: 0; -fx-hgap: 0; -fx-vgap: 0;");

        mapGrid.getRowConstraints().clear();
        mapGrid.getColumnConstraints().clear();

        for (int i = 0; i < COLS; i++) {
            ColumnConstraints colConst = new ColumnConstraints(TILE_SIZE);
            mapGrid.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < ROWS; i++) {
            RowConstraints rowConst = new RowConstraints(TILE_SIZE);
            mapGrid.getRowConstraints().add(rowConst);
        }
    }

    private void generateMap() {
        Random random = new Random();

        mapGrid.getChildren().clear();  // Clear all children before adding tiles

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane tilePane;
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1 || (row % 2 == 0 && col % 2 == 0)) {
                    mapData[row][col] = 'W';
                    tilePane = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
                } else if ((row <= 2 && col <= 2)) {
                    mapData[row][col] = '.';
                    tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                } else if (random.nextDouble() < 0.4) {
                    mapData[row][col] = 'B';
                    tilePane = ResourceLoader.createTexturedTile(breakableImg, TILE_SIZE);
                } else {
                    mapData[row][col] = '.';
                    tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                }
                tiles[row][col] = tilePane;
                mapGrid.add(tilePane, col, row);
            }
        }
    }

    private void handleKeyPress(KeyEvent event) {
        int dRow = 0, dCol = 0;

        switch (event.getCode()) {
            case W, UP -> dRow = -1;
            case S, DOWN -> dRow = 1;
            case A, LEFT -> dCol = -1;
            case D, RIGHT -> dCol = 1;
            case SPACE -> {
                bomb.place(player.getRow(), player.getCol());
                return;
            }
            default -> {
                return;
            }
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
        return mapData[row][col] == '.';
    }
}