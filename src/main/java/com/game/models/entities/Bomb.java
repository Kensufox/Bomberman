package com.game.models.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.game.controllers.GameMapController;
import com.game.utils.*;

import javafx.animation.PauseTransition;
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
    private final static int originalRange = 2;
    private int range = getOriginalRange();

    private final List<Player> players;
    private final GameMapController controller;

    private final Random random = new Random();
    private static final double POWER_UP_SPAWN_CHANCE = 0.3;

    // New constructor with a list of players
    public Bomb(GridPane mapGrid, char[][] mapData, StackPane[][] tiles, Image emptyImg, List<Player> players, GameMapController controller) {
        this.mapGrid = mapGrid;
        this.mapData = mapData;
        this.tiles = tiles;
        this.emptyImg = emptyImg;
        this.players = players;
        this.controller = controller;
    }


    /** 
     * @param row
     * @param col
     */
    public void place(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Bomb)));
        StackPane bombCell = ResourceLoader.createPixelatedImageNode(bombImg, TILE_SIZE, TILE_SIZE, 0, 0);

        if (mapData[row][col] == 'X') return;
        mapData[row][col] = 'X'; 
        mapGrid.add(bombCell, col, row);

        PauseTransition delay = new PauseTransition(Duration.seconds(2/ GameData.getGameSpeed()));
        delay.setOnFinished(e -> {
            mapGrid.getChildren().remove(bombCell);
            mapData[row][col] = '.';
            explode(row, col);
        });
        delay.play();
    }

    /** 
     * @param range
     */
    public void setRange(int range){
        this.range = range;
    }

    /** 
     * @return int
     */
    public int getRange() {
        return range;
    }

    /** 
     * @return int
     */
    public static int getOriginalRange() {
        return originalRange;
    }

    /** 
     * @return double
     */
    public static double getCOOLDOWN_SECONDS() {
        return COOLDOWN_SECONDS;
    }

    /** 
     * @param list
     * @param target
     * @return boolean
     */
    private boolean directionFinished(List<int[]> list, int[] target) {
        for (int[] d : list) {
            if (d[0] == target[0] && d[1] == target[1]) return true;
        }
        return false;
    }

    /** 
     * @param row
     * @param col
     */
    private void explode(int row, int col) {
        SFXPlayer.play(SFXLibrary.HURT);
        int[][] directions = {
            {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };
        List<int[]> finishedDirection = new ArrayList<>();

        for (int[] dir : directions) {
            int start = (dir[0] == 0 && dir[1] == 0) ? 0 : 1;
            for (int i = start; i <= range; i++) {
                int r = row + (dir[0] * i);
                int c = col + (dir[1] * i);

                if (r < 0 || r >= mapData.length || c < 0 || c >= mapData[0].length) break;
                if (directionFinished(finishedDirection, dir)) break;

                char tile = mapData[r][c];

                if (tile == 'W') {
                    finishedDirection.add(dir);
                    break;
                }

                if (tile == 'B') {
                    mapData[r][c] = '.';

                    StackPane newTile = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                    mapGrid.getChildren().remove(tiles[r][c]);
                    tiles[r][c] = newTile;
                    mapGrid.add(newTile, c, r);

                    if (random.nextDouble() < POWER_UP_SPAWN_CHANCE) {
                        controller.spawnPowerUpAt(r, c);
                    }
                }

                for (Player player : players) {
                    if (player != null && player.getState() == Player.State.ALIVE
                            && player.getRow() == r && player.getCol() == c) {
                        controller.killPlayer(player);
                    }
                }

                if (tile == '.' || tile == 'B') {
                    if (i > 0 && r == row && c == col) continue;
                    Image img;
                    if (i == 0) {
                        int up    = (mapData[r-1][c] == 'W') ? 1 : 0;
                        int down  = (mapData[r+1][c] == 'W') ? 1 : 0;
                        int left  = (mapData[r][c-1] == 'W') ? 1 : 0;
                        int right = (mapData[r][c+1] == 'W') ? 1 : 0;

                        int code = (up << 3) | (down << 2) | (left << 1) | right;

                        img = switch (code) {
                            case 15 -> new Image(ImageLibrary.CenterFire);
                            case 11 -> new Image(ImageLibrary.Down1Fire);
                            case 13 -> new Image(ImageLibrary.Left1Fire);
                            case 14 -> new Image(ImageLibrary.Right1Fire);
                            case 7  -> new Image(ImageLibrary.Up1Fire);
                            case 5  -> new Image(ImageLibrary.CenterULFire);
                            case 6  -> new Image(ImageLibrary.CenterURFire);
                            case 3  -> new Image(ImageLibrary.CenterUDFire);
                            case 9  -> new Image(ImageLibrary.CenterDLFire);
                            case 10 -> new Image(ImageLibrary.CenterDRFire);
                            case 12 -> new Image(ImageLibrary.CenterLRFire);
                            case 1  -> new Image(ImageLibrary.CenterUDLFire);
                            case 2  -> new Image(ImageLibrary.CenterUDRFire);
                            case 4  -> new Image(ImageLibrary.CenterULRFire);
                            case 8  -> new Image(ImageLibrary.CenterDLRFire);
                            default -> new Image(ImageLibrary.CenterFire);
                        };
                    } else if (i == range || mapData[r][c] == 'W') {
                        if (dir[1] > 0) img = new Image(ImageLibrary.Right1Fire);
                        else if (dir[1] < 0) img = new Image(ImageLibrary.Left1Fire);
                        else if (dir[0] < 0) img = new Image(ImageLibrary.Up1Fire);
                        else img = new Image(ImageLibrary.Down1Fire);
                    } else {
                        if (dir[1] > 0) img = new Image(ImageLibrary.Right2Fire);
                        else if (dir[1] < 0) img = new Image(ImageLibrary.Left2Fire);
                        else if (dir[0] < 0) img = new Image(ImageLibrary.Up2Fire);
                        else img = new Image(ImageLibrary.Down2Fire);
                    }

                    StackPane explosionPane = ResourceLoader.createTexturedTile(img, TILE_SIZE);
                    mapGrid.getChildren().remove(tiles[r][c]);
                    tiles[r][c] = explosionPane;
                    mapGrid.add(explosionPane, c, r);

                    PauseTransition clear = new PauseTransition(Duration.seconds(0.4 / GameData.getGameSpeed()));
                    clear.setOnFinished(e -> mapGrid.getChildren().remove(explosionPane));
                    clear.play();
                }
            }
        }
    }
}
