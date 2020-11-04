package com.SirBlobman.combatlogx.api.object;

public enum NoEntryMode {
    DISABLED,
    VULNERABLE,
    CANCEL_EVENT,
    KILL_PLAYER,
    TELEPORT_TO_ENEMY,
    KNOCKBACK_PLAYER;

    public static NoEntryMode parse(String string) {
        try {
            String value = string.toUpperCase();
            return valueOf(value);
        } catch(IllegalArgumentException | NullPointerException ex) {
            return DISABLED;
        }
    }
}
