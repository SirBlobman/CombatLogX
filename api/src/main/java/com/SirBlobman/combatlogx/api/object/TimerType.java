package com.SirBlobman.combatlogx.api.object;

public enum TimerType {
    GLOBAL, PERMISSION;

    public static TimerType parse(String string) {
        try {
            String value = string.toUpperCase();
            return valueOf(value);
        } catch(IllegalArgumentException | NullPointerException ex) {
            return GLOBAL;
        }
    }
}
