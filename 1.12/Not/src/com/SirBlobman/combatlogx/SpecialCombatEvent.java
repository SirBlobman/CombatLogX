package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.listener.event.CombatEvent;

import org.bukkit.entity.Player;

public class SpecialCombatEvent extends CombatEvent {
	private Player p;
	public SpecialCombatEvent(Player p, double damage) {
		super(p, null, damage, false);
		this.p = p;
	}
	
	public Player getPlayer() {return p;}
}