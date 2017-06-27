package com.SirBlobman.combatlog;

import com.SirBlobman.combatlog.listener.event.CombatEvent;

import org.bukkit.entity.Player;

public class SpecialCombatEvent extends CombatEvent {
	private Player p;
	public SpecialCombatEvent(Player p, double damage) {
		super(null, p, damage);	
		this.p = p;
	}
	
	public Player getPlayer() {return p;}
}