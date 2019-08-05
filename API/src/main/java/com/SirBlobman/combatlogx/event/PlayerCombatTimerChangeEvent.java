package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event fires for every second that the player is in combat<br/>
 * This can be used to create action bars, boss bars, and scoreboards
 *
 * @author SirBlobman
 */
public class PlayerCombatTimerChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    public static HandlerList getHandlerList() {return HANDLER_LIST;}
    public HandlerList getHandlers() {return HANDLER_LIST;}
    
    private final int timeLeft;
    public PlayerCombatTimerChangeEvent(Player player, int timeLeft) {
        super(player);
        this.timeLeft = timeLeft;
    }

    /**
     * @return The seconds left before the player escapes from combat
     */
    public int getSecondsLeft() {
        return this.timeLeft;
    }
}