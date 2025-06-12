package com.game;

import javafx.application.Platform;

public class JavaFXInitializer {

    private static boolean initialized = false;

    public static synchronized void init() {
        if (!initialized) {
            Platform.startup(() -> {});
            initialized = true;
        }
    }
}
