package io.github.palexdev.devutils.enums;

public enum MaterialThemeScheme {
    LIGHT("-light"),
    DARK("-dark"),
    UNKNOWN(""),
    ;

    private final String id;

    MaterialThemeScheme(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase() + " Scheme";
    }
}
