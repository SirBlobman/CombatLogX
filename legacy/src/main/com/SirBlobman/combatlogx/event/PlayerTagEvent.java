package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTagEvent extends PlayerEvent implements Cancellable {
    private static HandlerList HL = new HandlerList();    
    private boolean cancelled;
    private LivingEntity enemy;
    public PlayerTagEvent(Player p, LivingEntity enemy) {
        super(p);
        this.enemy = enemy;
    }

    public boolean isCancelled() {return cancelled;}
    public void setCancelled(boolean cancel) {this.cancelled = cancel;}
    public HandlerList getHandlers() {return HL;}
    public static HandlerList getHandlerList() {return HL;}
    public LivingEntity getEnemy() {return enemy;}
}