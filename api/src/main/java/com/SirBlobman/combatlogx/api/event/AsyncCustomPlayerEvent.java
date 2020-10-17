package com.SirBlobman.combatlogx.api.event;

import java.util.Objects;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class AsyncCustomPlayerEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private final Player player;
    public AsyncCustomPlayerEvent(Player player) {
        super(true);
        this.player = Objects.requireNonNull(player, "player must not be null!");
    }

    public final Player getPlayer() {
        return this.player;
    }
}