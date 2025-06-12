/**
 * Module tp.intro.javafx.
 *
 * <p>Ce module fournit l'application principale du jeu Bomberman développée avec JavaFX.</p>
 *
 * <p>Il dépend des modules JavaFX standards ainsi que du module {@code java.desktop} pour certaines fonctionnalités GUI supplémentaires :</p>
 * <ul>
 *   <li>{@code javafx.base}</li>
 *   <li>{@code javafx.controls}</li>
 *   <li>{@code javafx.graphics}</li>
 *   <li>{@code javafx.fxml}</li>
 *   <li>{@code javafx.media}</li>
 *   <li>{@code java.desktop}</li>
 * </ul>
 *
 * <p>Ce module exporte plusieurs packages contenant les contrôleurs, modèles d'entités,
 * utilitaires, ainsi que la logique liée aux bots et à la carte de jeu :</p>
 * <ul>
 *   <li><b>com.game</b> : package racine contenant les classes principales du jeu.</li>
 *   <li><b>com.game.controllers</b> : classes de contrôleurs MVC pour la gestion de l'interface.</li>
 *   <li><b>com.game.models.entities</b> : définition des entités du jeu (Bomb, Player, PowerUp, etc.).</li>
 *   <li><b>com.game.models.entities.bot</b> : classes liées aux bots (intelligence artificielle).</li>
 *   <li><b>com.game.models.map</b> : gestion et représentation de la carte de jeu.</li>
 *   <li><b>com.game.utils</b> : utilitaires divers pour la gestion du son, des images, des scores, etc.</li>
 * </ul>
 */
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

    exports com.game.utils;
}
