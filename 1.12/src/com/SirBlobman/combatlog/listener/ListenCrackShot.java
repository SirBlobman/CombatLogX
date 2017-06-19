package com.SirBlobman.combatlog.listener;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;

import com.SirBlobman.combatlog.listener.event.PlayerCombatEvent;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

public class ListenCrackShot implements Listener {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void dam(WeaponDamageEntityEvent e) {
		if(e.isCancelled()) return;
		double dam = e.getDamage();
		if(dam > 0) {
			Player p = e.getPlayer();
			Entity en = e.getVictim();
			LivingEntity le = null;
			if(en instanceof Projectile) {
				Projectile pe = (Projectile) en;
				ProjectileSource src = pe.getShooter();
				if(src instanceof LivingEntity) {
					le = (LivingEntity) src;
				} else return;
			} else {
				if(en instanceof LivingEntity) {le = (LivingEntity) en;}
				else return;
			}
			
			PlayerCombatEvent ce = new PlayerCombatEvent(p, le, dam);
			PM.callEvent(ce);
		}
	}
}