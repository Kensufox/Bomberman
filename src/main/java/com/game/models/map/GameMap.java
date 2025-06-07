package com.game.models.map;

import java.util.Objects;
import java.util.Random;

import com.game.utils.ImageLibrary;
import com.game.utils.ResourceLoader;

import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class GameMap {

    private final int TILE_SIZE = 40;
    private final int ROWS = 13;
    private final int COLS = 15;

    private final char[][] mapData = new char[ROWS][COLS]; // W = wall, B = breakable, . = empty
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];

    private final Image wallImg;
    private final Image breakableImg;
    private final Image emptyImg;

    public GameMap() {
        wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.InfWall)));
        breakableImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.WeakWall)));
        emptyImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Empty)));
    }

    public void setupMap(GridPane mapGrid) {
        setupGrid(mapGrid);
        generateMap(mapGrid);
    }

    private void setupGrid(GridPane mapGrid) {
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);
        mapGrid.setStyle("-fx-padding: 0; -fx-hgap: 0; -fx-vgap: 0;");

        mapGrid.getRowConstraints().clear();
        mapGrid.getColumnConstraints().clear();

        for (int i = 0; i < COLS; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(TILE_SIZE));
        }

        for (int i = 0; i < ROWS; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(TILE_SIZE));
        }
    }

    private void generateMap(GridPane mapGrid) {
        Random random = new Random();
        mapGrid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane tilePane;
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1 || (row % 2 == 0 && col % 2 == 0)) {
                    mapData[row][col] = 'W';
                    tilePane = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
                } else if ((row <= 2 && col <= 2) || row >= ROWS-3 && col >= COLS-3) {
                    mapData[row][col] = '.';
                    tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                } else if (random.nextDouble() < 0.7) {
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

    public void setupBackground(GameMap gameMap, GridPane backgroundGrid) {
        int rows = gameMap.getMapData().length;
        int cols = gameMap.getMapData()[0].length;
        int tileSize = gameMap.getTileSize();   

        backgroundGrid.getRowConstraints().clear();
        backgroundGrid.getColumnConstraints().clear();
        backgroundGrid.getChildren().clear();   

        for (int i = 0; i < cols; i++) {
            backgroundGrid.getColumnConstraints().add(new ColumnConstraints(tileSize));
        }
        for (int i = 0; i < rows; i++) {
            backgroundGrid.getRowConstraints().add(new RowConstraints(tileSize));
        }   

        String color1 = "#33b052"; // vert plus clair
        String color2 = "#257c3b"; // vert un peu foncé

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                StackPane tile = new StackPane();
                tile.setPrefSize(tileSize, tileSize);
                String color = ((row + col) % 2 == 0) ? color1 : color2;
                tile.setStyle("-fx-background-color: " + color + ";");
                backgroundGrid.add(tile, col, row);
            }
        }
    }

    public char[][] getMapData() {
        return mapData;
    }

    public StackPane[][] getTiles() {
        return tiles;
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    public Image getEmptyImg() {
        return emptyImg;
    }
}
