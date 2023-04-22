package com.github.sirblobman.combatlogx.api.object;

/**
 * The type of combat that a player was tagged with.
 */
public enum TagType {
    /**
     * Unknown type for being tagged. Usually occurs from the tag command.
     */
    UNKNOWN,

    /**
     * CombatTag was caused by another player or themselves.
     */
    PLAYER,

    /**
     * CombatTag was caused by a mob.
     */
    MOB,

    /**
     * CombatTag was caused by the Damage Tagger expansion.
     */
    DAMAGE,

    /**
     * CombatTag was caused by a custom mob from the MythicMobs plugin.
     */
    MYTHIC_MOB;
}
