package com.game.utils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MusicPlayerTest {

    @Test
    void testStopDoesNotThrowWhenNothingPlaying() {
        assertDoesNotThrow(MusicPlayer::stop);
    }

    @Test
    void testStopImmediateDoesNotThrow() {
        assertDoesNotThrow(MusicPlayer::stopImmediate);
    }

    @Test
    void testSetVolumeNoPlayer() {
        assertDoesNotThrow(() -> MusicPlayer.setVolume(0.5));
    }

    @Test
    void testIsPlayingReturnsFalseWhenNoMediaPlayer() {
        assertFalse(MusicPlayer.isPlaying());
    }
}
