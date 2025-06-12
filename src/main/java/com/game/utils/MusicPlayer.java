package com.game.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Utility class for playing background music tracks with support for different modes
 * such as normal play, loop, and random track selection. Provides fade-in and fade-out effects.
 */
public class MusicPlayer {
    /**
     * Enumeration of playback modes.
     */
    public enum Mode {
        /** Play the selected track once. */
        NORMAL,
        /** Loop the selected track indefinitely. */
        LOOP,
        /** Play random tracks from the music folder without repeating the same track consecutively. */
        RANDOM
    }

    private static MediaPlayer mediaPlayer;
    private static Timeline fadeOutTimeline;
    private static final int FADE_IN_SECONDS = 3;
    private static final int FADE_OUT_SECONDS = 2;
    private static Mode currentMode = Mode.NORMAL;
    private static String currentTrack = null;

    /**
     * Retrieves a list of all mp3 music files from the resource folder "/audio/music/".
     * 
     * @return List of music file names found in the folder
     * @throws RuntimeException if the folder cannot be found or is invalid
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
     * Starts playing the specified music file with the given playback mode.
     * If mode is RANDOM, selects a random track different from the current one.
     * Supports fade-in and fade-out effects.
     * 
     * @param filename the name of the music file to play, or null if mode is RANDOM (will select automatically)
     * @param mode the playback mode (NORMAL, LOOP, RANDOM)
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
     * Gradually increases the volume of the given MediaPlayer from 0 to 1 over the specified duration.
     * 
     * @param player the MediaPlayer to fade in
     * @param durationSeconds duration of the fade-in in seconds
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
     * Gradually decreases the volume of the given MediaPlayer to 0 over the specified duration,
     * then stops the playback if not in RANDOM mode.
     * 
     * @param player the MediaPlayer to fade out
     * @param durationSeconds duration of the fade-out in seconds
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

    /**
     * Stops the current playback with a fade-out effect.
     */
    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            fadeOut(mediaPlayer, FADE_OUT_SECONDS);
        }
    }

    /**
     * Immediately stops the current playback without any fade effect.
     */
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
     * Sets the volume of the current music playback.
     * 
     * @param volume volume level between 0.0 (mute) and 1.0 (max)
     */
    public static void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    /**
     * Checks if music is currently playing.
     * 
     * @return true if music is playing, false otherwise
     */
    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}
