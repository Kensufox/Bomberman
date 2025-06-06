package com.game.utils;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.HashMap;

public class SFXPlayer {
    private static final HashMap<String, AudioClip> sfxMap = new HashMap<>();

    public static void play(String filename) {
        AudioClip clip = sfxMap.get(filename);
        if (clip == null) {
            URL resource = SFXPlayer.class.getResource("/audio/" + filename);
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
            clip.setVolume(volume); // Volume entre 0.0 et 1.0
        }
    }

    public static void stopAll() {
        for (AudioClip clip : sfxMap.values()) {
            clip.stop();
        }
    }
}
