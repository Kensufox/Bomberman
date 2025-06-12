package com.game.models.map;

import com.game.utils.ImageLibrary;
import com.game.utils.ResourceLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Represents the visual and logical game map composed of tiles.
 * Each tile can be a wall, breakable wall, or empty space.
 * The map can be generated procedurally or loaded from a file.
 */
public class GameMap {

    private final int TILE_SIZE = 40;
    private final static int ROWS = 13;
    private final static int COLS = 15;

    private final char[][] mapData = new char[ROWS][COLS]; // W = wall, B = breakable, . = empty
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];

    private final Image wallImg;
    private final Image breakableImg;
    private final Image emptyImg;

    /**
     * Constructs a new GameMap, loading tile images using {@link ImageLibrary}.
     */
    public GameMap() {
        wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.InfWall)));
        breakableImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.WeakWall)));
        emptyImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Empty)));
    }

    /**
     * Initializes the game map and loads tile layout from a saved file.
     *
     * @param mapGrid The GridPane to which the tile nodes are added.
     */
    public void setupMap(GridPane mapGrid) {
        setupGrid(mapGrid);
        //generateMap(mapGrid);
        loadMapFromFile("src/main/resources/maps/saved-map.txt", mapGrid);
        //saveMapToFile("src/main/resources/maps/saved-map copy.txt", mapData);
    }

    /**
     * Configures the GridPane with the correct tile sizes.
     *
     * @param mapGrid The GridPane to configure.
     */
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

    /**
     * Procedurally generates a game map layout.
     * This can be used for random maps instead of loading from a file.
     *
     * @param mapGrid The GridPane to populate with generated tiles.
     */
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

    /**
     * Creates a background color pattern for the map grid (green checkerboard).
     *
     * @param gameMap        The game map instance for tile size reference.
     * @param backgroundGrid The GridPane to apply the background to.
     */
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

    /**
     * Saves the current map layout to a file. Breakables are saved as empty.
     *
     * @param filename The path to the output file.
     * @param mapData  The 2D character array representing the map layout to save.
     */
    public static void saveMapToFile(String filename, char[][] mapData) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(filename)) {
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    char cell = mapData[row][col];
                    // Only save 'W' and '.'; use '.' as default otherwise
                    writer.print((cell == 'W' || cell == '.') ? cell : '.');
                }
                writer.println();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Loads a map layout from a file, populating both the logic grid and visual tiles.
     *
     * @param filename The path to the saved map file.
     * @param mapGrid  The GridPane to populate.
     */
    public void loadMapFromFile(String filename, GridPane mapGrid) {
        try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File(filename))) {
            for (int row = 0; row < ROWS; row++) {
                if (!scanner.hasNextLine()) break;
                String line = scanner.nextLine();

                for (int col = 0; col < COLS; col++) {
                    StackPane tilePane;
                    char c = (col < line.length()) ? line.charAt(col) : '.';

                    if (c == 'W') {
                        mapData[row][col] = 'W';
                        tilePane = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
                    } else if ((row <= 2 && col <= 2) || row >= ROWS - 3 && col >= COLS - 3) {
                        // keep spawn zones empty
                        mapData[row][col] = '.';
                        tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                    } else {
                        // Randomly regenerate breakables in other empty spots
                        if (Math.random() < 0.7) {
                            mapData[row][col] = 'B';
                            tilePane = ResourceLoader.createTexturedTile(breakableImg, TILE_SIZE);
                        } else {
                            mapData[row][col] = '.';
                            tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                        }
                    }

                    tiles[row][col] = tilePane;
                    mapGrid.add(tilePane, col, row);
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * @return The logical character map (W, B, .).
     */
    public char[][] getMapData() {
        return mapData;
    }

    /**
     * @return The 2D array of visual tiles (StackPane).
     */
    public StackPane[][] getTiles() {
        return tiles;
    }

    /**
     * @return The size of a single tile in pixels.
     */
    public int getTileSize() {
        return TILE_SIZE;
    }

    /**
     * @return The image used for empty tiles.
     */
    public Image getEmptyImg() {
        return emptyImg;
    }
}
