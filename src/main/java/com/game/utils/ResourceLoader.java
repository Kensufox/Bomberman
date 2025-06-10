package com.game.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class ResourceLoader {

    /** 
     * @param texture
     * @param tileSize
     * @return StackPane
     */
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

/** 
 * @param img
 * @param width
 * @param height
 * @param xOffset
 * @param yOffset
 * @return StackPane
 */
public static StackPane createPixelatedImageNode(Image img, double width, double height, int xOffset, int yOffset) {
    double canvasWidth = width + Math.abs(xOffset);
    double canvasHeight = height + Math.abs(yOffset);

    Canvas canvas = new Canvas(canvasWidth, canvasHeight);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setImageSmoothing(false);

    // Compute where to draw the image on the canvas
    double drawX = xOffset >= 0 ? xOffset : 0;
    double drawY = yOffset >= 0 ? yOffset : 0;

    gc.drawImage(
        img,
        0, 0, img.getWidth(), img.getHeight(),  // source image
        drawX - xOffset, drawY - yOffset, width, height  // destination
    );

    StackPane pane = new StackPane(canvas);
    pane.setPrefSize(canvasWidth, canvasHeight);
    return pane;
}
}
