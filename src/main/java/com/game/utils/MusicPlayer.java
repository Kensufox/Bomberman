package com.game.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;

    public static void play(String filename, boolean loop) {
        stop();  // Arrête la musique actuelle si nécessaire
        Media media = new Media(MusicPlayer.class.getResource("/audio/" + filename).toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.play();
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume); // Volume entre 0.0 et 1.0
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
