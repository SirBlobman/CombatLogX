package com.github.sirblobman.combatlogx.api.object;

/**
 * The type of timer that will be used by CombatLogX
 */
public enum TimerType {
    /**
     * Every player will be tagged for the same amount of time.
     */
    GLOBAL,

    /**
     * Some players will have special combat times based on their permissions, others will use the global time.
     */
    PERMISSION
}
