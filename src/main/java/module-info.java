module DevUtils {
    //***** UI related *****//
    requires javafx.controls;

    requires MaterialFX;
    requires mfx.components;

    requires fr.brouillard.oss.cssfx;
    requires org.scenicview.scenicview;

    //***** Spring *****//
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    //***** Misc *****//
    requires java.prefs;

    //***** Exports *****//
    // Base
    exports io.github.palexdev.devutils;

    // Beans
    exports io.github.palexdev.devutils.beans;

    // Components
    exports io.github.palexdev.devutils.components;
    exports io.github.palexdev.devutils.components.dialogs;
    exports io.github.palexdev.devutils.components.settings;

    // Enums
    exports io.github.palexdev.devutils.enums;

    // Events
    exports io.github.palexdev.devutils.events;

    // Parsers
    exports io.github.palexdev.devutils.parsers;
    exports io.github.palexdev.devutils.parsers.base;

    // Services
    exports io.github.palexdev.devutils.services;
    exports io.github.palexdev.devutils.services.base;

    // Settings
    exports io.github.palexdev.devutils.settings;
    exports io.github.palexdev.devutils.settings.base;

    // Utils
    exports io.github.palexdev.devutils.utils;

    // Views
    exports io.github.palexdev.devutils.views;
    exports io.github.palexdev.devutils.views.base;
    exports io.github.palexdev.devutils.views.cards;
    exports io.github.palexdev.devutils.views.settings;

    //***** Opens *****//
    opens io.github.palexdev.devutils to spring.core;
    opens io.github.palexdev.devutils.views to spring.core;
}