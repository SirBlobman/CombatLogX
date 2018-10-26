package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerUntagEvent extends PlayerEvent implements Cancellable {
    public enum UntagCause {
        EXPIRE, QUIT, KICK, ENEMY_DEATH;
    }
    
    private static HandlerList HL = new HandlerList();
    
    private boolean cancelled = false;
    private final UntagCause cause;
    
    public PlayerUntagEvent(Player p, UntagCause cause) {
        super(p);
        this.cause = cause;
    }
    
    public UntagCause getCause() {
        return cause;
    }
    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return HL;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
