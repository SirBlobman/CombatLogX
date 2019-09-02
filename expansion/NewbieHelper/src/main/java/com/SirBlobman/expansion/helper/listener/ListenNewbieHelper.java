package com.SirBlobman.expansion.helper.listener;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.helper.config.ConfigNewbie;
import org.bukkit.projectiles.ProjectileSource;

public class ListenNewbieHelper implements Listener {
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		ConfigNewbie.setData(player, "username", player.getName());
		if(!player.hasPlayedBefore()) ConfigNewbie.setData(player, "protected", true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPVP(EntityDamageByEntityEvent e) {
		Entity ded = e.getEntity();
		Entity der = linkPet(linkProjectile(e.getDamager()));

		if(!(der instanceof Player) || !(ded instanceof Player)) return;
		
		Player damager = (Player) der;
		Player damaged = (Player) ded;
		
		boolean damagedProtected = ConfigNewbie.getData(damaged, "protected", false);
		if(damagedProtected) {
			long systemMillis = System.currentTimeMillis();
			long firstPlayed = damaged.getFirstPlayed();
			long subtract = (systemMillis - firstPlayed);
			int expireTime = ConfigNewbie.getOption("expire time", 30_000);
			if(subtract >= expireTime) {
				String message = ConfigLang.getWithPrefix("messages.expansions.newbie helper.disabled.expired");
				Util.sendMessage(damaged, message);
				ConfigNewbie.setData(damager, "protected", false);
				return;
			}
			
			e.setCancelled(true);
			String message = ConfigLang.getWithPrefix("messages.expansions.newbie helper.no pvp.other");
			Util.sendMessage(damager, message);
		}
		
		boolean damagerProtected = ConfigNewbie.getData(damager, "protected", false);
		if(damagerProtected) {
			String message = ConfigLang.getWithPrefix("messages.expansions.newbie helper.disabled.attacker");
			Util.sendMessage(damager, message);
			ConfigNewbie.setData(damager, "protected", false);
			return;
		}
	}

	private Entity linkProjectile(Entity entity) {
		if(!ConfigOptions.OPTION_LINK_PROJECTILES) return entity;
		if(!(entity instanceof Projectile)) return entity;

		Projectile projectile = (Projectile) entity;
		ProjectileSource shooter = projectile.getShooter();

		if(shooter instanceof Entity) return (Entity) shooter;
		return entity;
	}

	private Entity linkPet(Entity entity) {
		if(!ConfigOptions.OPTION_LINK_PETS) return entity;
		if(!(entity instanceof Tameable)) return entity;

		Tameable pet = (Tameable) entity;
		AnimalTamer owner = pet.getOwner();
		if(owner instanceof Entity) return (Entity) owner;
		return entity;
	}
}