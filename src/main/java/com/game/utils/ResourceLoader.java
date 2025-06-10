package com.game.utils;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * Utility class for creating JavaFX nodes with pixelated or textured images.
 */
public class ResourceLoader {

    /**
     * Creates a StackPane containing a Canvas with the given texture drawn as a tile.
     * The image is drawn scaled to the specified tileSize with image smoothing disabled
     * to preserve pixelated style.
     * 
     * @param texture the Image to be drawn as a texture
     * @param tileSize the width and height to scale the texture to (square)
     * @return a StackPane containing the pixelated textured tile
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
     * Creates a StackPane containing a Canvas with the specified portion of an image drawn
     * at a pixelated scale, with optional offsets for positioning.
     * Image smoothing is disabled to maintain pixelation.
     * 
     * @param img the source Image to draw
     * @param width the width to draw the image area
     * @param height the height to draw the image area
     * @param xOffset horizontal offset for drawing; can be negative
     * @param yOffset vertical offset for drawing; can be negative
     * @return a StackPane containing the pixelated image at the specified size and offset
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
            0, 0, img.getWidth(), img.getHeight(),  // source image rectangle
            drawX - xOffset, drawY - yOffset, width, height  // destination rectangle on canvas
        );

        StackPane pane = new StackPane(canvas);
        pane.setPrefSize(canvasWidth, canvasHeight);
        return pane;
    }
}
