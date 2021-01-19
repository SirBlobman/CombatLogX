package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CustomPlayerEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public CustomPlayerEvent(Player player) {
        super(player);
    }
}