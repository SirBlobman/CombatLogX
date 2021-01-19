package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Player;

/**
 * {@link PlayerCombatTimerChangeEvent} is an event that will be fired for every tick of a player's combat timer.
 * This can be used to update scoreboards/action bars/boss bars/etc...
 * @author SirBlobman
 */
public class PlayerCombatTimerChangeEvent extends CustomPlayerEventAsync {
    public PlayerCombatTimerChangeEvent(Player player) {
        super(player);
    }
}