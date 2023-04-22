package io.github.palexdev.devutils.enums;

public enum LogType {
    GENERIC(""),
    ERROR("[E]: "),
    WARN("[W]: "),
    OK("[OK]: "),
    ;

    private final String id;

    LogType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
