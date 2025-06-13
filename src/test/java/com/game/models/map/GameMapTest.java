package com.game.models.map;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameMapTest {

    @BeforeAll
    public static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown); // Initializes JavaFX toolkit
        latch.await();
    }

    @Test
    public void testSetupMapInitializesCorrectGrid() {
        GameMap gameMap = new GameMap();
        GridPane mapGrid = new GridPane();

        Platform.runLater(() -> {
            gameMap.setupMap(mapGrid);

            // Check mapData size
            char[][] data = gameMap.getMapData();
            assertEquals(13, data.length);
            assertEquals(15, data[0].length);

            // Check tile size
            StackPane[][] tiles = gameMap.getTiles();
            assertEquals(13, tiles.length);
            assertEquals(15, tiles[0].length);

            // Check GridPane has correct number of children (tiles)
            assertEquals(13 * 15, mapGrid.getChildren().size());

            // Check that each tile is non-null
            for (int row = 0; row < 13; row++) {
                for (int col = 0; col < 15; col++) {
                    assertNotNull(tiles[row][col], "Tile at (" + row + "," + col + ") should not be null");
                }
            }
        });
    }

    @Test
    public void testTileImageTypesFromMapFile() {
        GameMap gameMap = new GameMap();
        GridPane mapGrid = new GridPane();

        Platform.runLater(() -> {
            gameMap.loadMapFromFile("src/main/resources/maps/saved-map.txt", mapGrid);
            char[][] mapData = gameMap.getMapData();

            for (int row = 0; row < mapData.length; row++) {
                for (int col = 0; col < mapData[row].length; col++) {
                    char c = mapData[row][col];
                    assertTrue(c == 'W' || c == '.' || c == 'B',
                            "Unexpected tile char at (" + row + "," + col + "): " + c);
                }
            }
        });
    }

    @Test
    public void testBackgroundSetupCreatesCheckerboard() {
        GameMap gameMap = new GameMap();
        GridPane backgroundGrid = new GridPane();

        Platform.runLater(() -> {
            gameMap.setupBackground(gameMap, backgroundGrid);

            assertEquals(13 * 15, backgroundGrid.getChildren().size());
            assertEquals(13, backgroundGrid.getRowConstraints().size());
            assertEquals(15, backgroundGrid.getColumnConstraints().size());
        });
    }
}
