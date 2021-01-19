package com.github.sirblobman.combatlogx.api.object;

public enum NoEntryMode {
    /** All entries are allowed */
    DISABLED,

    /** The player will still take damage from their enemy even if they are in non-pvp areas */
    VULNERABLE,

    /** The event will be cancelled (e.g. undo move, prevent teleport) */
    CANCEL_EVENT,

    /** The player will be killed */
    KILL_PLAYER,

    /** The player will be teleported to their enemy */
    TELEPORT_TO_ENEMY,

    /** The player will be pushed away from the region with velocity. This option may trigger some anti-cheat plugins */
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
