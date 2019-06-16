package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerPunishEvent extends PlayerEvent implements Cancellable {
    public enum PunishReason {DISCONNECTED, KICKED, UNKNOWN}
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PunishReason punishReason;
    private final LivingEntity previousEnemy;
    private boolean cancelled = false;

    public PlayerPunishEvent(Player p, PunishReason reason, LivingEntity previousEnemy) {
        super(p);
        this.punishReason = reason;
        this.previousEnemy = previousEnemy;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public PunishReason getReason() {
        return punishReason;
    }
    
    public LivingEntity getPreviousEnemy() {
        return this.previousEnemy;
    }
}