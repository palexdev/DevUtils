package io.github.palexdev.devutils;

import java.net.URL;

public class Resources {

    //================================================================================
    // Constructors
    //================================================================================
    private Resources() {
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static URL get(String name) {
        return Resources.class.getResource(name);
    }

    public static String load(String name) {
        return get(name).toExternalForm();
    }

    public static String loadCss(String name) {
        return load("css/" + name);
    }
}
