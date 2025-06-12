package com.game.controllers;

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

import java.io.IOException;
import java.util.Objects;


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

    /** TextField input for naming the map before saving */
    @FXML private TextField mapNameField;

    /** Button to trigger map saving */
    @FXML private Button saveButton;

    /** Button to return to the main menu */
    @FXML private Button returnButton;

    /** Number of rows in the editable map grid */
    private final int ROWS = 13;

    /** Number of columns in the editable map grid */
    private final int COLS = 15;

    /** Size of each tile in pixels */
    private final int TILE_SIZE = 40;

    /** 2D array storing the map data where each cell is either wall ('W') or empty ('.') */
    private char[][] mapData = new char[ROWS][COLS];

    /** 2D array storing the visual tiles corresponding to mapData */
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];

    /** Image used for wall tiles */
    private final Image wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.InfWall)));

    /** Image used for empty tiles */
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

    /**
     * Toggles a cell between wall and empty tile, and updates the map data and visual representation.
     *
     * @param row the row of the cell to toggle
     * @param col the column of the cell to toggle
     */
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


    /**
     * Generates the level editor grid with default boundaries and editable inner cells.
     *
     * @param mapGrid the GridPane to populate with tiles
     */
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

    /**
     * Saves the current map to a text file using the entered name.
     */
    @FXML
    void saveMap() {
        String name = mapNameField.getText().trim();
        if (name.isEmpty()) return;

        GameMap.saveMapToFile("src/main/resources/maps/" + name + ".txt", mapData);

        returnToMenu();
    }

    /**
     * Returns the user to the main menu screen.
     */
    @FXML
    void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the current map data.
     *
     * @return 2D character array representing the map layout
     */
    public char[][] getMapData() {
        return mapData;
    }

    /**
     * Sets the internal map data.
     *
     * @param mapData the new map layout to apply
     */
    public void setMapData(char[][] mapData) {
        this.mapData = mapData;
    }

    /**
     * Returns the TextField used for entering the map name before saving.
     *
     * @return the mapNameField TextField
     */
    public TextField getMapNameField() {
        return mapNameField;
    }

    /**
     * Sets the TextField used for entering the map name before saving.
     *
     * @param mapNameField the TextField to set
     */
    public void setMapNameField(TextField mapNameField) {
        this.mapNameField = mapNameField;
    }

    /**
     * Returns the Button used to return to the main menu.
     *
     * @return the returnButton Button
     */
    public Button getReturnButton() {
        return returnButton;
    }

    /**
     * Sets the Button used to return to the main menu.
     *
     * @param returnButton the Button to set
     */
    public void setReturnButton(Button returnButton) {
        this.returnButton = returnButton;
    }

}
