package io.github.palexdev.devutils.services;

import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.model.ServiceModel;
import io.github.palexdev.devutils.views.View;
import javafx.scene.layout.Region;

public abstract class Service<M extends ServiceModel> {
    //================================================================================
    // Properties
    //================================================================================
    protected ServiceCard card;

    //================================================================================
    // Abstract Methods
    //================================================================================
    public abstract View view();

    public abstract ServiceCard toCard();

    public abstract M model();

    public abstract String description();

    //================================================================================
    // Methods
    //================================================================================
    public final Region getView() {
        return view().toRegion();
    }
}
