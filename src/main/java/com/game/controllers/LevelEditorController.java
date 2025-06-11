package com.game.controllers;

import java.io.IOException;
import java.util.Objects;

import com.game.models.map.GameMap;
import com.game.utils.ImageLibrary;
import com.game.utils.ResourceLoader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Controller class responsible for managing the game map, including player movements,
 * bomb placements, power-up handling, and transitions to the game over screen.
 */

public class LevelEditorController {

    /** The primary grid representing game elements */
    @FXML protected GridPane mapGrid;

    /** The background grid behind the game map */
    @FXML protected GridPane backgroundGrid;

    /** The current game map */
    protected GameMap gameMap;

    @FXML
    private TextField mapNameField;
    @FXML
    private Button saveButton, returnButton;

    private final int ROWS = 13;
    private final int COLS = 15;
    private final int TILE_SIZE = 40;

    private char[][] mapData = new char[ROWS][COLS];
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];
    
    private final Image wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.InfWall)));
    private final Image emptyImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Empty)));


    /**
     * Initializes the game map, players, bombs, and sets up input handling and the animation loop.
     */
    public void initialize() {
        this.gameMap = new GameMap();
        //gameMap.setupBackground(gameMap, backgroundGrid);
        //gameMap.setupMap(mapGrid);
        generateMapEditor(mapGrid);

        mapGrid.setFocusTraversable(true);
    }

    private void toggleCell(int row, int col) {
        mapGrid.getChildren().remove(tiles[row][col]);

        StackPane newTile;
        if (mapData[row][col] == '.') {
            mapData[row][col] = 'W';
            newTile = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
        } else {
            mapData[row][col] = '.';
            newTile = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
        }

        // Always attach the handler, regardless of type
        int finalRow = row;
        int finalCol = col;
        newTile.setOnMouseClicked(e -> toggleCell(finalRow, finalCol));

        tiles[row][col] = newTile;
        mapGrid.add(newTile, col, row);
    }

    private void generateMapEditor(GridPane mapGrid) {
        mapGrid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                StackPane tilePane;
                if (row == 0 || row == ROWS - 1 || col == 0 || col == COLS - 1) {
                    mapData[row][col] = 'W';
                    tilePane = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
                } else {
                    mapData[row][col] = '.';
                    tilePane = ResourceLoader.createTexturedTile(emptyImg, TILE_SIZE);
                    int finalRow = row;
                    int finalCol = col;
                    tilePane.setOnMouseClicked(e -> toggleCell(finalRow, finalCol));
                }
                tiles[row][col] = tilePane;
                mapGrid.add(tilePane, col, row);
            }
        }
    }

    @FXML
    private void saveMap() {
        String name = mapNameField.getText().trim();
        if (name.isEmpty()) return;

        GameMap.saveMapToFile("src/main/resources/maps/" + name + ".txt", mapData);

        returnToMenu();
    }

    @FXML
    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char[][] getMapData() {
        return mapData;
    }

    public void setMapData(char[][] mapData) {
        this.mapData = mapData;
    }
}
