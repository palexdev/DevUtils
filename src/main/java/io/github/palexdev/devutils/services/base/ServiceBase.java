package io.github.palexdev.devutils.services.base;

import io.github.palexdev.devutils.beans.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.function.Consumer;

public abstract class ServiceBase {
    //================================================================================
    // Properties
    //================================================================================
    private Consumer<Boolean> onInit = init -> {};
    private Runnable onReset = () -> {};
    protected final ObservableList<Log> logs = FXCollections.observableArrayList();

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract boolean init();
    protected abstract void reset();

    //================================================================================
    // Methods
    //================================================================================
    protected void onInit(boolean init) {
        if (onInit != null) onInit.accept(init);
    }

    protected void onReset() {
        if (onReset != null) onReset.run();
    }

    public void log(String message) {
        logs.add(Log.log(message));
    }

    public void error(String message) {
        logs.add(Log.error(message));
    }

    public void warn(String message) {
        logs.add(Log.warn(message));
    }

    public void ok(String message) {
        logs.add(Log.ok(message));
    }

    public void resetLogs() {
        logs.clear();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Consumer<Boolean> getOnInit() {
        return onInit;
    }

    public void setOnInit(Consumer<Boolean> onInit) {
        this.onInit = onInit;
    }

    public Runnable getOnReset() {
        return onReset;
    }

    public void setOnReset(Runnable onReset) {
        this.onReset = onReset;
    }

    public ObservableList<Log> getLogs() {
        return logs;
    }
}
