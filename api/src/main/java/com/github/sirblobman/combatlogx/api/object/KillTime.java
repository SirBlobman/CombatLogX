package com.github.sirblobman.combatlogx.api.object;

import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;

/**
 * The time that CombatLogX will kill players.
 * @see PunishConfiguration#getKillTime()
 */
public enum KillTime {
    /**
     * Kill the player the instant that they disconnect from the server.
     */
    QUIT,

    /**
     * Kill the player as soon as they log back in to the server.
     */
    JOIN,

    /**
     * Tell CombatLogX to not handle player killing.
     */
    NEVER
}
