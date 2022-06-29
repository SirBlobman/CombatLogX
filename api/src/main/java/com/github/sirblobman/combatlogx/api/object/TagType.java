package com.github.sirblobman.combatlogx.api.object;

public enum TagType {
    /**
     * Unknown type for being tagged. Usually occurs from the tag command.
     */
    UNKNOWN,

    /**
     * Tag was caused by another player or themselves.
     */
    PLAYER,

    /**
     * Tag was caused by a mob.
     */
    MOB,

    /**
     * Tag was caused by the Damage Tagger expansion.
     */
    DAMAGE,

    /**
     * Tag was caused by a custom mob from the MythicMobs plugin.
     */
    MYTHIC_MOB;
}
