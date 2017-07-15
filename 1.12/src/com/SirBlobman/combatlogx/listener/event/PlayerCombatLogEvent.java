package com.SirBlobman.combatlogx.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCombatLogEvent extends Event {
	private static final HandlerList HL = new HandlerList();
	private Player player;
	
	public PlayerCombatLogEvent(Player p) {
		this.player = p;
	}
	
	public Player getPlayer() {return player;}
	
	@Override
	public HandlerList getHandlers() {return HL;}
	public static HandlerList getHandlerList() {return HL;}
}