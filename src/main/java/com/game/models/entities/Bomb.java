package com.game.models.entities;

import java.util.Objects;

import com.game.utils.ResourceLoader;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Bomb {

    private static final int TILE_SIZE = 40;
    private final GridPane mapGrid;
    private final char[][] mapData;
    private final StackPane[][] tiles;
    private final Image emptyImg;

    public Bomb(GridPane mapGrid, char[][] mapData, StackPane[][] tiles, Image emptyImg) {
        this.mapGrid = mapGrid;
        this.mapData = mapData;
        this.tiles = tiles;
        this.emptyImg = emptyImg;
    }

    public void place(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bomb.png")));

        StackPane bombCell = ResourceLoader.createPixelatedImageNode(bombImg, TILE_SIZE, TILE_SIZE);

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
                {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            if (r >= 0 && r < mapData.length && c >= 0 && c < mapData[0].length) {
                if (mapData[r][c] == 'B') {
                    mapData[r][c] = '.';
                    StackPane oldTile = tiles[r][c];
                    mapGrid.getChildren().remove(oldTile);

                    
                    StackPane newTile = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                    tiles[r][c] = newTile;
                    mapGrid.add(newTile, c, r);
                }

                StackPane explosionPane = new StackPane();
                Canvas explosionCanvas = new Canvas(TILE_SIZE, TILE_SIZE);
                explosionCanvas.setOpacity(0.5);
                GraphicsContext gc = explosionCanvas.getGraphicsContext2D();
                gc.setFill(javafx.scene.paint.Color.YELLOW);
                gc.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
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
