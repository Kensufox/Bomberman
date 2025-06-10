package com.game.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LevelEditorController {

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField mapNameField;
    @FXML
    private Button saveButton, returnButton;

    private final int rows = 13;
    private final int cols = 15;
    private final int cellSize = 40;

    private int[][] mapGrid = new int[rows][cols];

    @FXML
    public void initialize() {
        drawGrid();
    }

    private void drawGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);

                if (row == 0 || col == 0 || row == rows - 1 || col == cols - 1) {
                    cell.setFill(Color.DARKGRAY); // Border
                    mapGrid[row][col] = 1; // Wall
                } else {
                    cell.setFill(Color.WHITE);
                    mapGrid[row][col] = 0; // Empty

                    int finalRow = row;
                    int finalCol = col;
                    cell.setOnMouseClicked(e -> toggleCell(finalRow, finalCol, cell));
                }

                cell.setStroke(Color.BLACK);
                gridPane.add(cell, col, row);
            }
        }
    }

    private void toggleCell(int row, int col, Rectangle cell) {
        if (mapGrid[row][col] == 0) {
            mapGrid[row][col] = 1;
            cell.setFill(Color.DARKGRAY);
        } else {
            mapGrid[row][col] = 0;
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
            for (int[] row : mapGrid) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu.fxml"));
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
