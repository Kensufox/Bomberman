package com.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main entry point for the Bomberman game application.
 * 
 * <p>This class initializes the JavaFX application, loads the main menu UI,
 * sets the application window properties, and starts background music.</p>
 */
public class GameApplication extends Application {

    /**
     * Starts the JavaFX application.
     * 
     * <p>Loads the main menu layout from FXML, sets up the primary stage, and starts playing
     * background music in random mode.</p>
     * 
     * @param primaryStage The main stage for the JavaFX application.
     * @throws Exception If loading the FXML file fails.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Bomberman | v0.0.1");
        primaryStage.setScene(new Scene(root, 600, 520));
        primaryStage.setResizable(false);

        // Play random background music track on start
        //MusicPlayer.play(MusicLibrary.ACTION3, MusicPlayer.Mode.RANDOM);

        primaryStage.show();
    }

    /**
     * The main method of the application.
     * 
     * <p>Launches the JavaFX application and initializes a timer that currently
     * schedules an empty task every second.</p>
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Empty task - placeholder for scheduled code
            }
        }, 0, 1000);
    }
}
