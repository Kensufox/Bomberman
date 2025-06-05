package com.game.controllers;

import java.util.Random;

import com.game.models.entities.Player;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameMapController {

    @FXML
    private GridPane mapGrid;

    private final int TILE_SIZE = 40;
    private final int ROWS = 13;
    private final int COLS = 15;

    private final char[][] mapData = new char[ROWS][COLS]; // W = wall, B = breakable, . = floor
    private final Rectangle[][] tiles = new Rectangle[ROWS][COLS];

    private Player player;
    private Circle playerIcon;

    public void initialize() {
        generateMap();

        // Create player at start
        player = new Player(1, 1);
        playerIcon = new Circle(TILE_SIZE / 2.5, Color.RED);
        mapGrid.add(playerIcon, player.getCol(), player.getRow());

        mapGrid.setFocusTraversable(true);
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);
        mapGrid.setPadding(javafx.geometry.Insets.EMPTY);

        mapGrid.setOnKeyPressed(this::handleKeyPress);
    }

    private void generateMap() {
        Random random = new Random();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setStrokeWidth(0); // remove border completely

                // Solid wall border and pillars
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1 || (row % 2 == 0 && col % 2 == 0)) {
                    tile.setFill(Color.DARKGRAY);
                    mapData[row][col] = 'W';
                }
                // Leave spawn area empty
                else if ((row <= 2 && col <= 2)) {
                    tile.setFill(Color.BEIGE);
                    mapData[row][col] = '.';
                }
                // Random weak walls
                else if (random.nextDouble() < 0.4) {
                    tile.setFill(Color.ORANGE);
                    mapData[row][col] = 'B';
                }
                // Empty tile
                else {
                    tile.setFill(Color.BEIGE);
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
            default -> { return; }
        }

        int newRow = player.getRow() + dRow;
        int newCol = player.getCol() + dCol;

        if (isWalkable(newRow, newCol)) {
            player.move(dRow, dCol);
            GridPane.setRowIndex(playerIcon, player.getRow());
            GridPane.setColumnIndex(playerIcon, player.getCol());
        }
    }

    private boolean isWalkable(int row, int col) {
        return mapData[row][col] == '.';
    }

    private void placeBomb(int row, int col) {
        Circle bomb = new Circle(TILE_SIZE / 3.0, Color.BLACK);
        mapGrid.add(bomb, col, row);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            mapGrid.getChildren().remove(bomb);
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
                mapGrid.add(explosion, c, r);

                PauseTransition clear = new PauseTransition(Duration.seconds(0.4));
                clear.setOnFinished(e -> mapGrid.getChildren().remove(explosion));
                clear.play();
            }
        }
    }
}
