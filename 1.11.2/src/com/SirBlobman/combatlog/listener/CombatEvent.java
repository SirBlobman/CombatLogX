package com.SirBlobman.combatlog.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatEvent extends Event implements Cancellable {
	private static final HandlerList HL = new HandlerList();
	private LivingEntity attack;
	private LivingEntity target;
	private double damage;
	private boolean cancel;
	
	public CombatEvent(LivingEntity attack, LivingEntity target, double damage) {
		this.attack = attack;
		this.target = target;
		this.damage = damage;
	}
	
	public LivingEntity getAttacker() {return attack;}
	public LivingEntity getTarget() {return target;}
	public double getDamage() {return damage;}
	
	@Override
	public HandlerList getHandlers() {return HL;}
	public static HandlerList getHandlerList() {return HL;}

	@Override
	public boolean isCancelled() {return cancel;}
	
	@Override
	public void setCancelled(boolean c) {this.cancel = c;}
}