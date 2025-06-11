package com.game.controllers;

import com.game.models.map.GameMap;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
/*
public class LevelEditorController {

    @FXML
    private GridPane mapGrip;
    @FXML
    private TextField mapNameField;
    @FXML
    private Button saveButton, returnButton;

    protected GameMap gameMap;

    private final int ROWS = 13;
    private final int COLS = 15;
    private final int TILE_SIZE = 40;

    private char[][] mapData = new char[ROWS][COLS];
    private final StackPane[][] tiles = new StackPane[ROWS][COLS];
    
    private final Image wallImg;
    private final Image breakableImg;
    private final Image emptyImg;

    public LevelEditorController() {
        wallImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.InfWall)));
        breakableImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.WeakWall)));
        emptyImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageLibrary.Empty)));
    }

    @FXML
    public void initialize() {
        drawGrid();
    }

    private void drawGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle cell = new Rectangle(TILE_SIZE, TILE_SIZE);
                StackPane tilePane;

                if (row == 0 || col == 0 || row == ROWS - 1 || col == COLS - 1) {
                    cell.setFill(Color.DARKGRAY); // Border
                    tilePane = ResourceLoader.createTexturedTile(wallImg, TILE_SIZE);
                    mapData[row][col] = 'W'; // Wall
                } else {
                    cell.setFill(Color.WHITE);
                    mapData[row][col] = '.'; // Empty

                    int finalRow = row;
                    int finalCol = col;
                    cell.setOnMouseClicked(e -> toggleCell(finalRow, finalCol, cell));
                }

                //cell.setStroke(Color.BLACK);
            
                tiles[row][col] = tilePane;
                mapGrid.add(tiles, col, row);
            }
        }
    }

    private void toggleCell(int row, int col, Rectangle cell) {
        if (mapData[row][col] == '.') {
            mapData[row][col] = 'W';
            cell.setFill(Color.DARKGRAY);
        } else {
            mapData[row][col] = '.';
            cell.setFill(Color.WHITE);
        }
    }

    @FXML
    private void saveMap() {
        String name = mapNameField.getText().trim();
        if (name.isEmpty()) return;

        File mapFile = new File("maps/" + name + ".txt");
        mapFile.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(mapFile)) {
            for (char[] row : mapData) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
} */


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

    /**
     * Initializes the game map, players, bombs, and sets up input handling and the animation loop.
     */
    public void initialize() {
        this.gameMap = new GameMap();
        gameMap.setupBackground(gameMap, backgroundGrid);
        gameMap.setupMap(mapGrid);

        mapGrid.setFocusTraversable(true);
    }
}
