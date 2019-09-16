package com.SirBlobman.expansion.helper.command;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CommandTogglePVP implements CommandExecutor, Listener {
	private static final List<UUID> NO_PVP = Util.newList();
	static boolean isPVPEnabled(Player player) {
		if(player == null) return true;

		UUID uuid = player.getUniqueId();
		return !NO_PVP.contains(uuid);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("combatlogx.command.pvptoggle")) {
			String message = ConfigLang.getWithPrefix("messages.commands.no permission").replace("{permission}", "combatlogx.command.pvptoggle");
			Util.sendMessage(sender, message);
			return true;
		}
		
		if(!(sender instanceof Player)) {
			String message = ConfigLang.getWithPrefix("messages.commands.not player");
			Util.sendMessage(sender, message);
			return true;
		}
		
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		if(NO_PVP.contains(uuid)) {
			NO_PVP.remove(uuid);
			Util.sendMessage(player, ConfigLang.getWithPrefix("messages.expansions.newbie helper.pvp.disabled"));
			return true;
		}
		
		NO_PVP.add(uuid);
		Util.sendMessage(player, ConfigLang.getWithPrefix("messages.expansions.newbie helper.pvp.enabled"));
		return true;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPVP(EntityDamageByEntityEvent e) {
		Entity der = e.getDamager();
		Entity ded = e.getEntity();
		if(!(der instanceof Player) || !(ded instanceof Player)) return;
		
		Player damager = (Player) der;
		UUID damagerId = damager.getUniqueId();
		if(NO_PVP.contains(damagerId)) {
			e.setCancelled(true);
			
			String message = ConfigLang.getWithPrefix("messages.expansions.newbie helper.no pvp.self");
			Util.sendMessage(damager, message);
			return;
		}

		Player damaged = (Player) ded;
		UUID damagedId = damaged.getUniqueId();
		if(NO_PVP.contains(damagedId)) {
			e.setCancelled(true);
			
			String message = ConfigLang.getWithPrefix("messages.expansions.newbie helper.no pvp.other");
			Util.sendMessage(damager, message);
		}
	}
}