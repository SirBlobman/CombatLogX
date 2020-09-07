package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.SirBlobman.api.utility.Validate;

public class CustomPlayerEventAsync extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    private final Player player;
    public CustomPlayerEventAsync(Player player) {
        super(true);
        this.player = Validate.notNull(player, "player must not be null!");
    }

    public final Player getPlayer() {
        return this.player;
    }
}