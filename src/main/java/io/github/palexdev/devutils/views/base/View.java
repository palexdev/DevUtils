package io.github.palexdev.devutils.views.base;

import io.github.palexdev.devutils.events.StageReadyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.springframework.context.event.EventListener;

public abstract class View<P extends Pane> {
    //================================================================================
    // Properties
    //================================================================================
    protected P root;

    //================================================================================
    // Constructors
    //================================================================================
    protected View() {}

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract P build();

    //================================================================================
    // Methods
    //================================================================================
    public Region toRegion() {
        return root;
    }

    //================================================================================
    // Event Handling
    //================================================================================
    @EventListener
    public void onStageReady(StageReadyEvent event) {
        root = build();
    }
}
