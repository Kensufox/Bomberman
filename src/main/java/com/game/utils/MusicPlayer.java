package com.game.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {
    private static MediaPlayer mediaPlayer;
    private static Timeline fadeOutTimeline;

    public static void play(String filename, boolean loop) {
    stopImmediate(); // stop sans fade pour éviter conflits

    Media media = new Media(MusicPlayer.class.getResource("/audio/music/" + filename).toString());
    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
    mediaPlayer.setVolume(0);

    if (!loop) {
        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getMedia().getDuration();
            int fadeOutSeconds = 2;
        
            Duration fadeOutStart = totalDuration.subtract(Duration.seconds(fadeOutSeconds));
            if (!fadeOutStart.lessThan(Duration.ZERO)) {
                Timeline delayFade = new Timeline(new KeyFrame(fadeOutStart, e -> fadeOut(mediaPlayer, fadeOutSeconds)));
                delayFade.play();
            }
        });
    }

    mediaPlayer.play();
    fadeIn(mediaPlayer, 3);
}

    private static void fadeIn(MediaPlayer player, int durationSeconds) {
        double targetVolume = 1.0;
        int steps = durationSeconds * 20;
        double volumeStep = targetVolume / steps;

        Timeline timeline = new Timeline();
        for (int i = 0; i <= steps; i++) {
            double volume = i * volumeStep;
            KeyFrame kf = new KeyFrame(Duration.millis(i * 50), e -> player.setVolume(volume));
            timeline.getKeyFrames().add(kf);
        }
        timeline.play();
    }

    private static void fadeOut(MediaPlayer player, int durationSeconds) {
        double initialVolume = player.getVolume();
        int steps = durationSeconds * 20;
        double volumeStep = initialVolume / steps;

        fadeOutTimeline = new Timeline();
        for (int i = 0; i <= steps; i++) {
            double volume = initialVolume - (i * volumeStep);
            KeyFrame kf = new KeyFrame(Duration.millis(i * 50), e -> player.setVolume(Math.max(0, volume)));
            fadeOutTimeline.getKeyFrames().add(kf);
        }
        fadeOutTimeline.setOnFinished(e -> {
            player.stop();
            fadeOutTimeline = null;
        });
        fadeOutTimeline.play();
    }

    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            fadeOut(mediaPlayer, 2); // Fade-out sur 2 secondes
        }
    }

    public static void stopImmediate() {
        if (fadeOutTimeline != null) {
            fadeOutTimeline.stop();
            fadeOutTimeline = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
