package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CombatTimerChangeEvent extends PlayerEvent {
    private static HandlerList HL = new HandlerList();
    private final long seconds;

    public CombatTimerChangeEvent(Player player, long timeLeft) {
        super(player);
        this.seconds = timeLeft;
    }

    public long secondsLeft() {
        return seconds;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return HL;
    }
}
