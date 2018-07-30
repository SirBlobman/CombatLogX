package com.SirBlobman.combatlogx.listener;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

public class CombatListener implements Listener {
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPreTag(PlayerPreTagEvent e) {
		Player p = e.getPlayer();
		TagType type = e.getTaggedBy();
		
		World world = p.getWorld();
		String wname = world.getName().toLowerCase();
		if(ConfigOptions.OPTION_DISABLED_WORLDS.contains(wname)) e.setCancelled(true);
		
		if(ConfigOptions.COMBAT_BYPASS_ALLOW) {
			String perm = ConfigOptions.COMBAT_BYPASS_PERMISSION;
			if(p.hasPermission(perm)) e.setCancelled(true);
		}
		
		if(type == TagType.MOB) {
			if(!ConfigOptions.COMBAT_MOBS) e.setCancelled(true);
			else {
				LivingEntity enemy = e.getEnemy();
				if(enemy != null) {
					List<String> blacklist = ConfigOptions.COMBAT_MOBS_BLACKLIST;
					String entityType = enemy.getType().name();
					if(blacklist.contains(entityType)) e.setCancelled(true);
				}
			}
		}
		
		if(type == TagType.PLAYER) {
			LivingEntity lenemy = e.getEnemy();
			if(lenemy != null && (lenemy instanceof Player)) {
				Player enemy = (Player) lenemy;
				if(enemy.equals(p) && !ConfigOptions.COMBAT_SELF) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR) 
	public void onTag(PlayerTagEvent e) {
		Player p = e.getPlayer();
		if(ConfigOptions.COMBAT_SUDO) {
			List<String> commands = ConfigOptions.COMBAT_SUDO_COMMANDS;
			Stream<String> stream = commands.stream();
			
			stream.filter(command -> command.startsWith("[CONSOLE]")).forEach(command -> {
				String cmd = command.substring(9).replace("{player}", p.getName());
				Bukkit.dispatchCommand(Util.CONSOLE, cmd);
			});
			
			stream.filter(command -> command.startsWith("[PLAYER]")).forEach(command -> {
				String cmd = command.substring(8).replace("{player}", p.getName());
				p.performCommand(cmd);
			});
			
			stream.filter(command -> command.startsWith("[OP]")).forEach(command -> {
				String cmd = command.substring(4).replace("{player}", p.getName());
				
				if(p.isOp()) p.performCommand(cmd);
				else {
					p.setOp(true);
					p.performCommand(cmd);
					p.setOp(false);
				}
			});
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity le = e.getEntity();
		OfflinePlayer op = CombatUtil.getByEnemy(le);
		if(op != null && op.isOnline()) {
			Player p = op.getPlayer();
			if(CombatUtil.isInCombat(p) && ConfigOptions.COMBAT_UNTAG_ON_ENEMY_DEATH) {
				String msg = ConfigLang.getWithPrefix("messages.combat.enemy death");
				Util.sendMessage(p, msg);
				CombatUtil.untag(p, UntagReason.EXPIRE);
			}
		}
		
		if(le instanceof Player) {
			Player p = (Player) le;
			if(CombatUtil.isInCombat(p) && ConfigOptions.COMBAT_UNTAG_ON_SELF_DEATH) {
				CombatUtil.untag(p, UntagReason.EXPIRE);
			}
		}
	}
}