package com.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/game/views/game_map.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Bomberman | v0.0.1");
        primaryStage.setScene(new Scene(root, 600, 520));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
