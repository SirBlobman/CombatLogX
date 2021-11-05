package com.github.sirblobman.combatlogx.api.object;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

/**
 * If you are going to implement this class, don't forget to register your instance with the timer manager.
 *
 * @see ITimerManager
 * @see ITimerManager#addUpdaterTask(TimerUpdater)
 */
public interface TimerUpdater {
    /**
     * This method is executed every tick while a player is in combat.
     *
     * @param player         The player for this update.
     * @param timeLeftMillis The amount of time left in combat for this player.
     */
    void update(Player player, long timeLeftMillis);
    
    /**
     * This method is executed whenever a player is untagged.
     *
     * @param player The player for this removal.
     */
    void remove(Player player);
}
