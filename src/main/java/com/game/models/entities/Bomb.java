package com.game.models.entities;

import com.game.controllers.GameMapController;
import com.game.models.entities.bot.PlacedBomb;
import com.game.utils.*;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a bomb entity in the game. Handles the placement, explosion,
 * and visual effects of the bomb, as well as player damage and potential
 * power-up generation after destroying breakable tiles.
 */
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



    private final List<PlacedBomb> activeBombs = new ArrayList<>();

    // New constructor with a list of players
    /**
     * Constructs a Bomb object with necessary map data and game references.
     *
     * @param mapGrid    The GridPane representing the game map.
     * @param mapData    A 2D char array representing tile types on the map.
     * @param tiles      A 2D array of StackPanes representing each map tile.
     * @param emptyImg   The image used for empty (cleared) tiles.
     * @param players    A list of players in the game.
     * @param controller Reference to the GameMapController for interaction.
     */
    public Bomb(GridPane mapGrid, char[][] mapData, StackPane[][] tiles, Image emptyImg, List<Player> players, GameMapController controller) {
        this.mapGrid = mapGrid;
        this.mapData = mapData;
        this.tiles = tiles;
        this.emptyImg = emptyImg;
        this.players = players;
        this.controller = controller;
    }

    /**
     * Places a bomb at the specidsfied grid location and schedules its explosion.
     *
     * @param row The row index on the map.
     * @param col The column index on the map.
     */
    public void place(int row, int col) {
        Image bombImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Bomb)));
        StackPane bombCell = ResourceLoader.createPixelatedImageNode(bombImg, TILE_SIZE, TILE_SIZE, 0, 0);

        PlacedBomb bomb = new PlacedBomb(row, col, System.currentTimeMillis() + 2/GameData.getGameSpeed(), range);
        activeBombs.add(bomb);

        if (mapData[row][col] == 'X') return;
        mapData[row][col] = 'X'; 
        mapGrid.add(bombCell, col, row);

        PauseTransition delay = new PauseTransition(Duration.seconds(2/ GameData.getGameSpeed()));
        delay.setOnFinished(e -> {

            mapGrid.getChildren().remove(bombCell);
            mapData[row][col] = '.';
            explode(row, col);
            activeBombs.remove(bomb);
        });
        delay.play();
    }

    /**
     * Sets the explosion range of the bomb. Don't allow negative or null value
     *
     * @param range The number of tiles the explosion will extend.
     */
    public void setRange(int range) {
        if (range <= 0) {
            this.range = 1;
        } else {
            this.range = range;
        }
    }

    /**
     * Gets the current explosion range of the bomb.
     *
     * @return The number of tiles the explosion will extend.
     */
    public int getRange() {
        return range;
    }

    /**
     * Gets the default explosion range.
     *
     * @return The default explosion range.
     */
    public static int getOriginalRange() {
        return originalRange;
    }

    /**
     * Gets the cooldown period (in seconds) for placing another bomb.
     *
     * @return Cooldown time in seconds.
     */
    public static double getCOOLDOWN_SECONDS() {
        return COOLDOWN_SECONDS;
    }

    /**
     * Returns a copy of the list of bombs currently active on the map.
     *
     * @return A new list containing active PlacedBomb objects.
     */
    public List<PlacedBomb> getActiveBombs() {
        return new ArrayList<>(activeBombs);
    }

    /**
     * Checks if a particular direction has already been processed (finished).
     *
     * @param list   List of directions that are finished.
     * @param target Direction to check.
     * @return true if the target direction is already finished, false otherwise.
     */
    private boolean directionFinished(List<int[]> list, int[] target) {
        for (int[] d : list) {
            if (d[0] == target[0] && d[1] == target[1]) return true;
        }
        return false;
    }

    /**
     * Triggers the explosion logic from the bomb's position,
     * updating the map and affecting players and tiles.
     *
     * @param row The row index where the bomb exploded.
     * @param col The column index where the bomb exploded.
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


        // Tester la victoire après avoir traité tous les joueurs touchés
        List<Player> alivePlayers = new ArrayList<>();
        for (Player p : players) {
            if (p != null && p.getState() == Player.State.ALIVE) {
                alivePlayers.add(p);
            }
        }

        // Si il ne reste qu'un seul joueur vivant, il a gagné
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.get(0);

            // Enregistrer la victoire si un profil est sélectionné
            if (PlayerManager.hasCurrentPlayer() && winner.getPlayerConnected()) {
                PlayerManager.recordGameWon();

                // Ici vous pouvez ajouter d'autres actions de fin de partie
                System.out.println("Le joueur " + PlayerManager.getCurrentPlayer().getNom() + " a gagné !");
            }


        }
    }
}
