package com.SirBlobman.combatlog.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.SirBlobman.combatlog.Combat;

public class PlayerUntagEvent extends Event {
	public enum UntagCause {DEATH, LOGOUT, TIME}
	
	private static final HandlerList hl = new HandlerList();
	private Player player;
	private UntagCause cause;
	
	public PlayerUntagEvent(Player player, UntagCause cause) {
		this.player = player;
		this.cause = cause;
		Combat.remove(player);
	}
	
	public Player getPlayer() {return player;}
	public UntagCause getCause() {return cause;}
	
	@Override
	public HandlerList getHandlers() {return hl;}
	public static HandlerList getHandlerList() {return hl;}
}