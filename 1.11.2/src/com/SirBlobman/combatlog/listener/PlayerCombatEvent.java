package com.SirBlobman.combatlog.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PlayerCombatEvent extends CombatEvent {
	private Player player;
	private LivingEntity enemy;
	private boolean starter;
	
	public PlayerCombatEvent(Player attack, LivingEntity target, double damage) {
		super(attack, target, damage);
		this.player = attack;
		this.enemy = target;
		this.starter = true;
	}
	
	public PlayerCombatEvent(LivingEntity attack, Player target, double damage) {
		super(attack, target, damage);
		this.player = target;
		this.enemy = attack;
		this.starter = false;
	}
	
	public Player getPlayer() {return player;}
	public LivingEntity getEnemy() {return enemy;}
	public boolean isPlayerAttacker() {return starter;}
}