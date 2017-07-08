package com.SirBlobman.combatlog.listener.event;

import org.bukkit.entity.Damageable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatEvent extends Event implements Cancellable {
	private final Damageable attacker, target;
	private final double damage;
	private boolean cancel;
	public CombatEvent(Damageable d1, Damageable d2, double damage, boolean isFirstAttacker) {
		this.damage = damage;
		if(isFirstAttacker) {
			this.attacker = d1;
			this.target = d2;
		} else {
			this.attacker = d2;
			this.target = d1;
		}
	}
	
	public Damageable getDamager() {return attacker;}
	public Damageable getDamaged() {return target;}
	public double getDamage() {return damage;}

	private static final HandlerList HL = new HandlerList();
	public static HandlerList getHandlerList() {return HL;}
	public HandlerList getHandlers() {return HL;}
	public boolean isCancelled() {return cancel;}
	public void setCancelled(boolean c) {this.cancel = c;}
}