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

    public static String loadViewCss(String name) {
        return load("css/views/" + name);
    }

    public static String loadComponentCss(String name) {
        return load("css/components/" + name);
    }

    public static String loadSettingsCss(String name) {
        return load(("css/settings/" + name));
    }
}
