package io.github.palexdev.devutils.settings.base;

/*
public record Setting(String name, String defValue, Settings container) {

    //================================================================================
    // Static Methods
    //================================================================================
    public static Setting of(String name, String defValue, Settings container) {
        return new Setting(name, defValue, container);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public void set(String value) {
        container.set(this, value);
    }

    public String get() {
        return container.get(this);
    }

    public void reset() {
        container.reset(this);
    }
}
*/

public abstract class Setting<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected final String name;
    protected final T defaultValue;
    protected final Settings container;

    //================================================================================
    // Constructors
    //================================================================================
    protected Setting(String name, T defaultValue, Settings container) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.container = container;
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    public abstract T get();
    public abstract void set(T val);

    //================================================================================
    // Methods
    //================================================================================
    public void reset() {
        set(defaultValue);
    }

    //================================================================================
    // Getters
    //================================================================================
    public String name() {
        return name;
    }

    public T defValue() {
        return defaultValue;
    }

    public Settings container() {
        return container;
    }
}
