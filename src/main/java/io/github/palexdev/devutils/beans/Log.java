package io.github.palexdev.devutils.beans;

import io.github.palexdev.devutils.enums.LogType;

public record Log(LogType type, String message) {
    public static Log of(LogType type, String message) {
        return new Log(type, message);
    }

    public static Log log(String message) {
        return new Log(LogType.GENERIC, message);
    }

    public static Log error(String message) {
        return new Log(LogType.ERROR, message);
    }

    public static Log warn(String message) {
        return new Log(LogType.WARN, message);
    }

    public static Log ok(String message) {
        return new Log(LogType.OK, message);
    }

    @Override
    public String toString() {
        return type.id() + message;
    }
}
