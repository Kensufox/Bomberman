package com.game.models.entities;

import java.util.List;
import java.util.Objects;

import com.game.controllers.GameMapController;
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
    private static final double COOLDOWN_SECONDS = 1.0;
    private final GridPane mapGrid;
    private final char[][] mapData;
    private final StackPane[][] tiles;
    private final Image emptyImg;
    private boolean canPlaceBomb = true;
    private int range = 1;

    private final List<Player> players;
    private final GameMapController controller;

    // New constructor with a list of players
    public Bomb(GridPane mapGrid, char[][] mapData, StackPane[][] tiles, Image emptyImg, List<Player> players, GameMapController controller) {
        this.mapGrid = mapGrid;
        this.mapData = mapData;
        this.tiles = tiles;
        this.emptyImg = emptyImg;
        this.players = players;
        this.controller = controller;
    }

    public void tryPlaceBomb(int row, int col) {
        if (!canPlaceBomb) {
            return;
        }

        canPlaceBomb = false;
        PauseTransition cooldown = new PauseTransition(Duration.seconds(COOLDOWN_SECONDS));
        cooldown.setOnFinished(e -> canPlaceBomb = true);
        cooldown.play();

        place(row, col);
    }

    public void place(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bomb.png")));
        StackPane bombCell = ResourceLoader.createPixelatedImageNode(bombImg, TILE_SIZE, TILE_SIZE, 0, 0);

        if (mapData[row][col] == 'X') return;
        mapData[row][col] = 'X'; 
        mapGrid.add(bombCell, col, row);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            mapGrid.getChildren().remove(bombCell);
            mapData[row][col] = '.';
            explode(row, col);
        });
        delay.play();
    }

    public void setRange(int range){
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    private void explode(int row, int col) {
        int[][] directions = {
            {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int i = 0; i <= range; i++) {
            for (int[] dir : directions) {
                int r = row + (dir[0]*i);
                int c = col + (dir[1]*i);

                if (r >= 0 && r < mapData.length && c >= 0 && c < mapData[0].length) {
                    if (mapData[r][c] == 'B') {
                        mapData[r][c] = '.';
                        StackPane oldTile = tiles[r][c];
                        mapGrid.getChildren().remove(oldTile);

                        StackPane newTile = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                        tiles[r][c] = newTile;
                        mapGrid.add(newTile, c, r);
                    }

                    // Check all players
                    for (Player player : players) {
                        if (player != null && player.getState() == Player.State.ALIVE && player.getRow() == r && player.getCol() == c) {
                            controller.killPlayer(player);
                        }
                    }

                    if (mapData[r][c] == 'B' || mapData[r][c] == '.'){
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
    }
}
