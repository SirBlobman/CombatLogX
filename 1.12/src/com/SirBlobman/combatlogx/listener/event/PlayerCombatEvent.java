package com.SirBlobman.combatlogx.listener.event;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

public class PlayerCombatEvent extends CombatEvent {
	private final Player player;
	private final Damageable attacker, target;
	private final boolean isPlayerAttacker;
	private final double damage;
	public PlayerCombatEvent(Player p, Damageable d, double damage, boolean isPlayerAttacker) {
		super(p, d, damage, isPlayerAttacker);
		this.player = p;
		this.damage = damage;
		this.isPlayerAttacker = isPlayerAttacker;
		if(isPlayerAttacker) {
			this.attacker = p;
			this.target = d;
		} else {
			this.attacker = d;
			this.target = p;
		}
	}
	
	@Deprecated
	public Player getPlayer() {return player;}
	public Damageable getDamager() {return attacker;}
	public Damageable getDamaged() {return target;}
	public double getDamage() {return damage;}
	public boolean isPlayerAttacker() {return isPlayerAttacker;}
}