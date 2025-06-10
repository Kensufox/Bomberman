package com.game;

import java.util.Timer;
import java.util.TimerTask;

import com.game.utils.MusicLibrary;
import com.game.utils.MusicPlayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {

    /** 
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Bomberman | v0.0.1");
        primaryStage.setScene(new Scene(root, 600, 520));
        primaryStage.setResizable(false);
        MusicPlayer.play(MusicLibrary.ACTION3, MusicPlayer.Mode.RANDOM);
        primaryStage.show();
    }

    /** 
     * @param args
     */
    public static void main(String[] args) {

        launch(args);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 1000);
    }
}
