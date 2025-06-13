package com.game;

import javafx.application.Platform;

public class JavaFXInitializer {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // Already initialized, ignore
            }
            initialized = true;
        }
    }
}
