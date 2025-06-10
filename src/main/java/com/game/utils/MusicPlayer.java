package com.game.utils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {
    public enum Mode {
        NORMAL, LOOP, RANDOM
    }

    private static MediaPlayer mediaPlayer;
    private static Timeline fadeOutTimeline;
    private static final int FADE_IN_SECONDS = 3;
    private static final int FADE_OUT_SECONDS = 2;
    private static Mode currentMode = Mode.NORMAL;
    private static String currentTrack = null;

    /** 
     * @return List<String>
     */
    private static List<String> getMusicFiles() {
        URL folderURL = MusicPlayer.class.getResource("/audio/music/");
        if (folderURL == null) {
            throw new RuntimeException("Music folder not found: /audio/music/");
        }
        File folder = new File(folderURL.getFile());
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Invalid music folder path: " + folder.getAbsolutePath());
        }

        return Arrays.stream(Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".mp3"))))
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /** 
     * @param filename
     * @param mode
     */
    public static void play(String filename, Mode mode) {
        stopImmediate();
        currentMode = mode;

        if (mode == Mode.RANDOM) {
            List<String> files = getMusicFiles();
            if (files.isEmpty()) {
                System.err.println("No music files found in /audio/music/");
                return;
            }
            // Avoid repeating the same track
            List<String> choices = files.stream()
                    .filter(name -> !name.equals(currentTrack))
                    .collect(Collectors.toList());
            currentTrack = choices.isEmpty() ? files.get(0) : choices.get(new Random().nextInt(choices.size()));
            filename = currentTrack;
        } else {
            currentTrack = filename;
        }

        URL fileURL = MusicPlayer.class.getResource("/audio/music/" + filename);
        if (fileURL == null) {
            System.err.println("Music file not found: " + filename);
            return;
        }

        Media media = new Media(fileURL.toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0);

        switch (mode) {
            case LOOP -> mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            case NORMAL, RANDOM -> mediaPlayer.setCycleCount(1);
        }

        mediaPlayer.setOnReady(() -> {
            Duration totalDuration = mediaPlayer.getMedia().getDuration();
            Duration fadeOutStart = totalDuration.subtract(Duration.seconds(FADE_OUT_SECONDS));

            if (!fadeOutStart.lessThan(Duration.ZERO)) {
                Timeline fadeTrigger = new Timeline(new KeyFrame(fadeOutStart, e -> fadeOut(mediaPlayer, FADE_OUT_SECONDS)));
                fadeTrigger.play();
            }
        });
        
        mediaPlayer.setOnEndOfMedia(() -> {
            fadeOutTimeline = null; // cleanup just in case
            if (currentMode == Mode.RANDOM) {
                play(null, Mode.RANDOM); // launch another random track
            }
        });

        mediaPlayer.play();
        fadeIn(mediaPlayer, FADE_IN_SECONDS);
    }

    /** 
     * @param player
     * @param durationSeconds
     */
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

    /** 
     * @param player
     * @param durationSeconds
     */
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
            if (currentMode != Mode.RANDOM) {
                player.stop();
            }
            fadeOutTimeline = null;
        });
        fadeOutTimeline.play();
    }

    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            fadeOut(mediaPlayer, FADE_OUT_SECONDS);
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

    /** 
     * @param volume
     */
    public static void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    /** 
     * @return boolean
     */
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
