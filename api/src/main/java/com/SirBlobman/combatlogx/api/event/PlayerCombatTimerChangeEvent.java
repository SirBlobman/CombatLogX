package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.Player;

/**
 * PlayerCombatTimerChangeEvent is an event that will be fired for every second of a player's combat timer.
 * This can be used to update scoreboards/action bars/boss bars/etc...
 *
 * @author SirBlobman
 */
public class PlayerCombatTimerChangeEvent extends AsyncCustomPlayerEvent {
    private final int timeLeft;
    public PlayerCombatTimerChangeEvent(Player player, int timeLeft) {
        super(player);
        this.timeLeft = timeLeft;
    }

    /**
     * @return The seconds left before the player escapes from combat.
     */
    public int getSecondsLeft() {
        return this.timeLeft;
    }
}