package io.github.palexdev.devutils.model;

import io.github.palexdev.devutils.views.View;

public class EmptyModel implements ServiceModel {
    //================================================================================
    // Singleton
    //================================================================================
    public static final EmptyModel EMPTY = new EmptyModel();

    //================================================================================
    // Constructors
    //================================================================================
    private EmptyModel() {
    }

    @Override
    public View view() {
        return null;
    }
}
