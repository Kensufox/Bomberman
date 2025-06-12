package com.game.utils;

import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.HashMap;

/**
 * Utility class to manage playback of sound effects (SFX) in the game.
 * 
 * <p>It caches loaded audio clips to optimize performance and supports
 * global volume control.</p>
 */
public class SFXPlayer {
    private static final HashMap<String, AudioClip> sfxMap = new HashMap<>();
    private static double globalVolume = 1; // Default volume (range 0.0 to 1.0)

    /**
     * Plays the specified sound effect by filename.
     * If the clip is not already loaded, it will be loaded and cached.
     * 
     * @param filename The filename of the sound effect to play.
     */
    public static void play(String filename) {
        AudioClip clip = sfxMap.get(filename);
        if (clip == null) {
            URL resource = SFXPlayer.class.getResource("/audio/SFX/" + filename);
            if (resource == null) {
                System.err.println("SFX file not found: " + filename);
                return;
            }
            clip = new AudioClip(resource.toString());
            clip.setVolume(globalVolume);
            sfxMap.put(filename, clip);
        }
        clip.play();
    }

    /**
     * Sets the volume for all loaded sound effects.
     * Volume value should be between 0.0 (mute) and 1.0 (max).
     * 
     * @param volume The new volume level.
     */
    public static void setVolume(double volume) {
        for (AudioClip clip : sfxMap.values()) {
            clip.setVolume(volume);
        }
    }

    /**
     * Stops playback of all currently playing sound effects.
     */
    public static void stopAll() {
        for (AudioClip clip : sfxMap.values()) {
            clip.stop();
        }
    }

    /**
     * Returns the current global volume setting.
     * 
     * @return The global volume (0.0 to 1.0).
     */
    public static double getGlobalVolume() {
        return globalVolume;
    }

    /**
     * Sets the global volume that will be applied to new and existing sound effects.
     * 
     * @param globalVolume The global volume level (0.0 to 1.0).
     */
    public static void setGlobalVolume(double globalVolume) {
        SFXPlayer.globalVolume = globalVolume;
    }

    /**
     * Adds a hover sound effect to a JavaFX Button.
     * When the mouse pointer enters the button, the select sound effect is played.
     * 
     * @param button The Button to add the hover sound effect to.
     */
    public static void setupHoverSound(Button button) {
        if (button != null) {
            button.setOnMouseEntered(e -> play(SFXLibrary.SELECT));
        }
    }
}
