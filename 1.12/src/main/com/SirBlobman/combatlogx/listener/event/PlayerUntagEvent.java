package com.SirBlobman.combatlogx.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerUntagEvent extends PlayerEvent {
    public enum UntagCause {EXPIRE, QUIT, KICK;}
    private static HandlerList HL = new HandlerList();
    
    private final UntagCause cause;
    public PlayerUntagEvent(Player p, UntagCause cause) {
        super(p);
        this.cause = cause;
    }
    
    public UntagCause getCause() {return cause;}
    
    @Override
    public HandlerList getHandlers() {return getHandlerList();}
    public static HandlerList getHandlerList() {return HL;}
}
