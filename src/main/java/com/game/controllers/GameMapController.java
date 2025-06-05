package com.game.controllers;

import java.util.Objects;
import java.util.Random;

import com.game.models.entities.Player;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    private final int TILE_SIZE = 40;
    private final int ROWS = 13;
    private final int COLS = 15;

    private final char[][] mapData = new char[ROWS][COLS]; // W = wall, B = breakable, . = empty
    private final Rectangle[][] tiles = new Rectangle[ROWS][COLS];

    private Player player;
    private StackPane playerCell;

    public void initialize() {
        setupGrid();
        generateMap();

        // Load player image
        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));

        // Create a canvas for the player icon sized to TILE_SIZE x TILE_SIZE
        Canvas playerCanvas = new Canvas(TILE_SIZE, TILE_SIZE);
        GraphicsContext gc = playerCanvas.getGraphicsContext2D();
        gc.setImageSmoothing(false); // nearest neighbor scaling
        gc.drawImage(playerImg, 0, 0, playerImg.getWidth(), playerImg.getHeight(),
                     0, 0, TILE_SIZE, TILE_SIZE);

        player = new Player(1, 1);

        // Use a StackPane to hold the canvas and place in the grid
        playerCell = new StackPane(playerCanvas);
        playerCell.setPrefSize(TILE_SIZE, TILE_SIZE);
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

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setStrokeWidth(0); // No border
                tile.setStroke(null);

                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1 || (row % 2 == 0 && col % 2 == 0)) {
                    tile.setFill(Color.DARKGRAY); // Solid wall
                    mapData[row][col] = 'W';
                } else if ((row <= 2 && col <= 2)) {
                    tile.setFill(Color.BEIGE); // Spawn area
                    mapData[row][col] = '.';
                } else if (random.nextDouble() < 0.4) {
                    tile.setFill(Color.ORANGE); // Breakable wall
                    mapData[row][col] = 'B';
                } else {
                    tile.setFill(Color.BEIGE); // Empty
                    mapData[row][col] = '.';
                }

                tiles[row][col] = tile;
                mapGrid.add(tile, col, row);
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
                placeBomb(player.getRow(), player.getCol());
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
            GridPane.setRowIndex(playerCell, player.getRow());
            GridPane.setColumnIndex(playerCell, player.getCol());
        }
    }

    private boolean isWalkable(int row, int col) {
        return mapData[row][col] == '.';
    }

    private void placeBomb(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bomb.png")));

        // Create canvas with tile size
        Canvas canvas = new Canvas(TILE_SIZE, TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Disable smoothing for nearest neighbor effect
        gc.setImageSmoothing(false);

        // Draw bomb image scaled to TILE_SIZE x TILE_SIZE
        gc.drawImage(bombImg, 0, 0, bombImg.getWidth(), bombImg.getHeight(), 
                     0, 0, TILE_SIZE, TILE_SIZE);

        StackPane bombCell = new StackPane(canvas);
        bombCell.setPrefSize(TILE_SIZE, TILE_SIZE);
        mapGrid.add(bombCell, col, row);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            mapGrid.getChildren().remove(bombCell);
            explode(row, col);
        });
        delay.play();
    }

    private void explode(int row, int col) {
        int[][] directions = {
                {0, 0},  // center
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}  // up, down, left, right
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                if (mapData[r][c] == 'B') {
                    mapData[r][c] = '.';
                    tiles[r][c].setFill(Color.BEIGE);
                }

                Rectangle explosion = new Rectangle(TILE_SIZE, TILE_SIZE, Color.YELLOW);
                explosion.setOpacity(0.5);
                StackPane explosionPane = new StackPane(explosion);
                explosionPane.setPrefSize(TILE_SIZE, TILE_SIZE);
                mapGrid.add(explosionPane, c, r);

                PauseTransition clear = new PauseTransition(Duration.seconds(0.4));
                clear.setOnFinished(e -> mapGrid.getChildren().remove(explosionPane));
                clear.play();
            }
        }
    }
}
