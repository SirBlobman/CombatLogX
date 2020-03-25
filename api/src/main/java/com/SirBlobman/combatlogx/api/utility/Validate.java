package com.SirBlobman.combatlogx.api.utility;

public final class Validate {
    public static <T> T notNull(T value, String message) {
        if(value == null) throw new IllegalArgumentException(message);
        return value;
    }
}