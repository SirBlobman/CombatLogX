package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

abstract class CustomPlayerEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public CustomPlayerEvent(Player player) {
        super(player);
    }
}