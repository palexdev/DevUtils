package io.github.palexdev.devutils.components.settings;

import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.VBox;

public class SettingsPane extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty title = new SimpleStringProperty();
    private final ObservableList<SettingDescriptor<?>> settings = FXCollections.observableArrayList();

    //================================================================================
    // Constructors
    //================================================================================
    public SettingsPane(String title) {
        setTitle(title);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add("settings-pane");
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Skin<?> createDefaultSkin() {
        return new SettingsPaneSkin(this);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public static class SettingsPaneSkin extends SkinBase<SettingsPane> {
        private final VBox box;
        private final Label label;

        public SettingsPaneSkin(SettingsPane pane) {
            super(pane);

            label = new Label();
            label.getStyleClass().add("title");
            label.textProperty().bind(pane.titleProperty());
            box = new VBox(label);
            pane.getSettings().forEach(s -> box.getChildren().add(s.toComponent()));
            getChildren().add(box);
            addListeners();
        }

        private void addListeners() {
            SettingsPane pane = getSkinnable();
            ObservableList<SettingDescriptor<?>> settings = pane.getSettings();
            settings.addListener((ListChangeListener<? super SettingDescriptor<?>>) c -> {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        int s = c.getFrom();
                        box.getChildren().remove(s);
                    }
                    if (c.wasAdded()) {
                        int s = c.getFrom();
                        int e = c.getTo();
                        for (int i = s; i < e; i++) {
                            SettingDescriptor<?> sd = settings.get(i);
                            box.getChildren().add(i, sd.toComponent());
                        }
                    }
                }
            });
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public ObservableList<SettingDescriptor<?>> getSettings() {
        return settings;
    }

    public SettingsPane addSettings(SettingDescriptor<?>... sds) {
        settings.addAll(sds);
        return this;
    }
}
