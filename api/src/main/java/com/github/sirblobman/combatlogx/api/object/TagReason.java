package com.github.sirblobman.combatlogx.api.object;

/**
 * The reason for putting a player into combat.
 */
public enum TagReason {
    /**
     * Unknown reason for being tagged. Usually occurs from the tag command.
     * This can also occur from the Damage Tagger expansion.
     */
    UNKNOWN,

    /**
     * The player was damaged by an enemy.
     */
    ATTACKED,

    /**
     * The player caused an enemy to take damage.
     */
    ATTACKER;
}
