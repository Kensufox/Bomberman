package com.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello JavaFX!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 960, 720);
        stage.setScene(scene);
        stage.setTitle("Bomberman");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
