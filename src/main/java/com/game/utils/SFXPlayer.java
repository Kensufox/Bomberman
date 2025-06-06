package com.game.utils;

import java.net.URL;
import java.util.HashMap;

import javafx.scene.media.AudioClip;

public class SFXPlayer {
    private static final HashMap<String, AudioClip> sfxMap = new HashMap<>();

    public static void play(String filename) {
        AudioClip clip = sfxMap.get(filename);
        if (clip == null) {
            URL resource = SFXPlayer.class.getResource("/audio/SFX/" + filename);
            if (resource == null) {
                System.err.println("SFX file not found: " + filename);
                return;
            }
            clip = new AudioClip(resource.toString());
            sfxMap.put(filename, clip);
        }
        clip.play();
    }

    public static void setVolume(double volume) {
        for (AudioClip clip : sfxMap.values()) {
            clip.setVolume(volume); // Volume between 0.0 and 10.0
        }
    }

    public static void stopAll() {
        for (AudioClip clip : sfxMap.values()) {
            clip.stop();
        }
    }
}
