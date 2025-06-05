package com.game.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class ResourceLoader {

    public static StackPane createTexturedTile(Image texture, double tileSize) {
        Canvas canvas = new Canvas(tileSize, tileSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(texture, 0, 0, texture.getWidth(), texture.getHeight(),
                     0, 0, tileSize, tileSize);
        StackPane pane = new StackPane(canvas);
        pane.setPrefSize(tileSize, tileSize);
        return pane;
    }

    public static StackPane createPixelatedImageNode(Image img, double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(img, 0, 0, img.getWidth(), img.getHeight(),
                     0, 0, width, height);
        StackPane pane = new StackPane(canvas);
        pane.setPrefSize(width, height);
        return pane;
    }
}
