package io.github.palexdev.devutils.views;

import javafx.scene.layout.Region;

public abstract class View {

    //================================================================================
    // Constructors
    //================================================================================
    protected View() {
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract void build();

    public abstract Region toRegion();
}
