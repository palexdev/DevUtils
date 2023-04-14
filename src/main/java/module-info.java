module DevUtils {
    requires java.prefs;

    requires transitive javafx.base;
    requires transitive javafx.controls;

    requires MaterialFX;
    requires mfx.components;
    requires mfx.localization;
    requires VirtualizedFX;
    requires fr.brouillard.oss.cssfx;

    // TODO change this
    requires org.scenicview.scenicview;

    // Components
    exports io.github.palexdev.devutils.components;

    // Model
    exports io.github.palexdev.devutils.model;

    // Settings
    exports io.github.palexdev.devutils.settings;

    // Utils
    exports io.github.palexdev.devutils.utils;

    // Views
    exports io.github.palexdev.devutils.views;

    // Root
    exports io.github.palexdev.devutils;
    exports io.github.palexdev.devutils.settings.descriptors;
}