package com.SirBlobman.expansion.helper.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.helper.config.ConfigNewbie;

public class ListenNewbieHelper implements Listener {
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		ConfigNewbie.setData(player, "username", player.getName());
		if(!player.hasPlayedBefore()) ConfigNewbie.setData(player, "protected", true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPVP(EntityDamageByEntityEvent e) {
		Entity der = e.getDamager();
		Entity ded = e.getEntity();
		if(!(der instanceof Player) || !(ded instanceof Player)) return;
		
		Player damager = (Player) der;
		Player damaged = (Player) ded;
		
		boolean damagedProtected = ConfigNewbie.getData(damaged, "protected", false);
		if(damagedProtected) {
			long systemMillis = System.currentTimeMillis();
			long firstPlayed = damaged.hasPlayedBefore() ? damaged.getFirstPlayed() : systemMillis;
			long subtract = (systemMillis - firstPlayed);
			if(subtract >= ConfigNewbie.getOption("expire time", 30_000)) {
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
}