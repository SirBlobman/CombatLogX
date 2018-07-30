package com.SirBlobman.combatlogx.listener;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;

public class AttackListener implements Listener {
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onAttack(EntityDamageByEntityEvent e) {
		Entity damaged = e.getEntity();
		Entity damager = e.getDamager();
		
		if((damager instanceof Projectile) && ConfigOptions.OPTION_LINK_PROJECTILES) {
			Projectile p = (Projectile) damager;
			ProjectileSource ps = p.getShooter();
			if(ps instanceof Entity) {damager = (Entity) ps;}
		}
		
		if((damager instanceof Tameable) && ConfigOptions.OPTION_LINK_PETS) {
			Tameable t = (Tameable) damager;
			AnimalTamer at = t.getOwner();
			if(at instanceof Entity) {damager = (Entity) at;}
		}
		
		if(damaged instanceof LivingEntity && damager instanceof LivingEntity) {
			if(damaged instanceof Player) {
				Player p = (Player) damaged;
				LivingEntity enemy = (damager instanceof LivingEntity) ? (LivingEntity) damager : null;
				TagType type = (damager instanceof Player) ? TagType.PLAYER : ((damager instanceof LivingEntity) ? TagType.MOB : TagType.UNKNOWN);
				TagReason reason = TagReason.ATTACKED;
				CombatUtil.tag(p, enemy, type, reason);
			}
			
			if(damager instanceof Player) {
				Player p = (Player) damager;
				LivingEntity enemy = (damaged instanceof LivingEntity) ? (LivingEntity) damaged : null;
				TagType type = (damaged instanceof Player) ? TagType.PLAYER : ((damaged instanceof LivingEntity) ? TagType.MOB : TagType.UNKNOWN);
				TagReason reason = TagReason.ATTACKER;
				CombatUtil.tag(p, enemy, type, reason);
			}
		}
	}
}