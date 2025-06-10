open module tp.intro.javafx {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    exports com.game;
    exports com.game.controllers;
    exports com.game.models.entities;
    exports com.game.models.entities.bot;
    exports com.game.models.map;
}
