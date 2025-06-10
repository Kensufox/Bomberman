package com.game.utils;

import java.net.URL;
import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;

public class SFXPlayer {
    private static final HashMap<String, AudioClip> sfxMap = new HashMap<>();
    private static double globalVolume = 0.5; // Valeur par défaut (entre 0.0 et 1.0)
/*
    public static void preload(String filename) {
        if (!sfxMap.containsKey(filename)) {
            URL resource = SFXPlayer.class.getResource("/audio/SFX/" + filename);
            if (resource != null) {
                AudioClip clip = new AudioClip(resource.toString());

                // Lecture muette pour forcer le chargement du clip en mémoire
                clip.setVolume(0.0);
                clip.play();

                // Attendre brièvement pour s'assurer qu'il est chargé
                new Thread(() -> {
                    try {
                        Thread.sleep(100); // 100ms suffit généralement
                    } catch (InterruptedException ignored) {}
                    clip.setVolume(globalVolume);
                }).start();

                sfxMap.put(filename, clip);
            }
        }
    }*/

    /** 
     * @param filename
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
            clip.setVolume(globalVolume); // Appliquer le volume défini
            sfxMap.put(filename, clip);

        }
        clip.play();
    }

    /** 
     * @param volume
     */
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

    public static double getGlobalVolume() {
        return globalVolume;
    }

    public static void setGlobalVolume(double globalVolume) {
        SFXPlayer.globalVolume = globalVolume;
    }

    public static void setupHoverSound(Button button) {
        if (button != null) {
            button.setOnMouseEntered(e -> play(SFXLibrary.SELECT));
        }
    }

    //public static void setupHoverSound(Button button) {
    //    if (button != null) {
    //        button.setOnMouseEntered(e -> play(SFXLibrary.SELECT));
    //    }
    //}
}
