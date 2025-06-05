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
import javafx.util.Duration;

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

        // Load player image and create pixelated canvas icon
        Image playerImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/player.png")));
        playerCell = createPixelatedImageNode(playerImg, TILE_SIZE, TILE_SIZE);

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

    private StackPane createTexturedTile(Image texture) {
        Canvas canvas = new Canvas(TILE_SIZE, TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(texture, 0, 0, texture.getWidth(), texture.getHeight(),
                     0, 0, TILE_SIZE, TILE_SIZE);

        StackPane pane = new StackPane(canvas);
        pane.setPrefSize(TILE_SIZE, TILE_SIZE);
        return pane;
    }

    private StackPane createPixelatedImageNode(Image img, double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(img, 0, 0, img.getWidth(), img.getHeight(),
                     0, 0, width, height);
        StackPane pane = new StackPane(canvas);
        pane.setPrefSize(width, height);
        return pane;
    }

    private void generateMap() {
        Random random = new Random();

        mapGrid.getChildren().clear();  // Clear all children before adding tiles

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane tilePane;
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1 || (row % 2 == 0 && col % 2 == 0)) {
                    mapData[row][col] = 'W';
                    tilePane = createTexturedTile(wallImg);
                } else if ((row <= 2 && col <= 2)) {
                    mapData[row][col] = '.';
                    tilePane = createTexturedTile(emptyImg);
                } else if (random.nextDouble() < 0.4) {
                    mapData[row][col] = 'B';
                    tilePane = createTexturedTile(breakableImg);
                } else {
                    mapData[row][col] = '.';
                    tilePane = createTexturedTile(emptyImg);
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
            // Move playerCell in the grid
            GridPane.setRowIndex(playerCell, player.getRow());
            GridPane.setColumnIndex(playerCell, player.getCol());
        }
    }

    private boolean isWalkable(int row, int col) {
        return mapData[row][col] == '.';
    }

    private void placeBomb(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bomb.png")));

        StackPane bombCell = createPixelatedImageNode(bombImg, TILE_SIZE, TILE_SIZE);
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
                    // Update tile texture to empty
                    StackPane oldTile = tiles[r][c];
                    mapGrid.getChildren().remove(oldTile);

                    StackPane newTile = createTexturedTile(emptyImg);
                    tiles[r][c] = newTile;
                    mapGrid.add(newTile, c, r);
                }

                StackPane explosionPane = new StackPane();
                Canvas explosionCanvas = new Canvas(TILE_SIZE, TILE_SIZE);
                explosionCanvas.setOpacity(0.5);
                explosionCanvas.getGraphicsContext2D().setFill(javafx.scene.paint.Color.YELLOW);
                explosionCanvas.getGraphicsContext2D().fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                explosionPane.getChildren().add(explosionCanvas);
                explosionPane.setPrefSize(TILE_SIZE, TILE_SIZE);

                mapGrid.add(explosionPane, c, r);

                PauseTransition clear = new PauseTransition(Duration.seconds(0.4));
                clear.setOnFinished(e -> mapGrid.getChildren().remove(explosionPane));
                clear.play();
            }
        }
    }
}
