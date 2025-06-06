package com.game;

import com.game.models.entities.Bomb;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;

public class GameApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_menu.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Bomberman | v0.0.1");
        primaryStage.setScene(new Scene(root, 600, 520));
        primaryStage.show();
    }

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
