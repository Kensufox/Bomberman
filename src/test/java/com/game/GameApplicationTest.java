package com.game;
import com.game.utils.MusicLibrary;
import com.game.utils.MusicPlayer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GameApplicationTest {

    @BeforeAll
    public static void setupJavaFX() {
        JavaFXInitializer.init();
    }

    @Test
    public void testStart_setsUpStageAndPlaysMusic() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                GameApplication app = new GameApplication();

                try (MockedStatic<MusicPlayer> mockedMusicPlayer = org.mockito.Mockito.mockStatic(MusicPlayer.class)) {
                    app.start(stage);

                    // Vérifications sur le stage
                    assertEquals("Bomberman | v0.0.1", stage.getTitle());
                    Scene scene = stage.getScene();
                    assertNotNull(scene);
                    assertEquals(600, scene.getWidth(), 0.1);
                    assertEquals(520, scene.getHeight(), 0.1);
                    assertFalse(stage.isResizable());

                    // Vérifie que la musique a été jouée avec les bons paramètres
                    mockedMusicPlayer.verify(() ->
                            MusicPlayer.play(MusicLibrary.ACTION3, MusicPlayer.Mode.RANDOM)
                    );
                }
            } catch (Exception e) {
                fail("Exception during test: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        // Attend la fin de l’exécution sur le thread JavaFX (timeout 5s)
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Timeout waiting for JavaFX thread");
        }
    }
}
