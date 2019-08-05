package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerPunishEvent extends PlayerEvent implements Cancellable {
	public enum PunishReason {DISCONNECTED, KICKED, UNKNOWN}
	
	private static final HandlerList HANDLER_LIST = new HandlerList();
	public static HandlerList getHandlerList() {return HANDLER_LIST;}
	public HandlerList getHandlers() {return HANDLER_LIST;}

	private final PunishReason punishReason;
	private final LivingEntity previousEnemy;

	public PlayerPunishEvent(Player player, PunishReason reason, LivingEntity previousEnemy) {
		super(player);
		this.punishReason = reason;
		this.previousEnemy = previousEnemy;
	}

	private boolean cancelled = false;
	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}


	/**
	 * @return The {@link PunishReason} that a player should be punished
	 */
	public PunishReason getReason() {
		return this.punishReason;
	}

	/**
	 * 
	 * @return The last known entity that was the player's enemy
	 */
	public LivingEntity getPreviousEnemy() {
		return this.previousEnemy;
	}
}