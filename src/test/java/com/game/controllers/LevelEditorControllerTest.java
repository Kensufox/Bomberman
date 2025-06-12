package com.game.controllers;

import com.game.JavaFXInitializer;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;





class LevelEditorControllerTest {

    private LevelEditorController controller;

    @BeforeAll
    public static void setUpOnce() {
        JavaFXInitializer.init();
    }

    @BeforeEach
    void setUp() {
        controller = new LevelEditorController();
        // Mock FXML fields
        controller.mapGrid = new GridPane();
        controller.backgroundGrid = new GridPane();
        controller.setMapNameField(new TextField());
        controller.setReturnButton(Mockito.mock(javafx.scene.control.Button.class));
    }

    @Test
    void testInitializeCreatesGameMapAndGrid() {
        controller.initialize();
        assertNotNull(controller.getMapData());
        assertEquals(13, controller.getMapData().length);
        assertEquals(15, controller.getMapData()[0].length);
    }

    @Test
    void testGenerateMapEditorSetsBoundariesAsWalls() throws Exception {
        controller.initialize();
        char[][] map = controller.getMapData();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (row == 0 || row == map.length - 1 || col == 0 || col == map[0].length - 1) {
                    assertEquals('W', map[row][col], "Boundary should be wall");
                }
            }
        }
    }

    @Test
    void testGetAndSetMapData() {
        char[][] newMap = new char[13][15];
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 15; j++) {
                newMap[i][j] = '.';
            }
        }
        controller.setMapData(newMap);
        assertArrayEquals(newMap, controller.getMapData());
    }

    @Test
    void testSaveMapDoesNothingIfNameEmpty() {
        controller.setMapNameField(new TextField(" "));
        // Should not throw or save
        assertDoesNotThrow(() -> controller.saveMap());
    }

    @Test
    void testSaveMapCallsReturnToMenuIfNameNotEmpty() {
        controller.setMapNameField(new TextField("testmap"));
        LevelEditorController spyController = Mockito.spy(controller);
        Mockito.doNothing().when(spyController).returnToMenu();
        spyController.saveMap();
        Mockito.verify(spyController).returnToMenu();
    }
}