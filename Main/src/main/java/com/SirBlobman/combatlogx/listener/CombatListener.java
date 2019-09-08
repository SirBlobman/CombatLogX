package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;
import java.util.UUID;

public class CombatListener implements Listener {
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void beforeTag(PlayerPreTagEvent e) {
		Player player = e.getPlayer();
		TagType tagType = e.getTaggedBy();

		World world = player.getWorld();
		String worldName = world.getName();
		if(ConfigOptions.OPTION_DISABLED_WORLDS.contains(worldName)) {
			e.setCancelled(true);
			return;
		}

		if(ConfigOptions.COMBAT_BYPASS_ALLOW && player.hasPermission(ConfigOptions.COMBAT_BYPASS_PERMISSION)) {
			e.setCancelled(true);
			return;
		}

		if(tagType == TagType.MOB) {
			if(!ConfigOptions.COMBAT_MOBS) {
				e.setCancelled(true);
				return;
			}

			LivingEntity enemy = e.getEnemy();
			if(enemy != null) {
				String enemyType = enemy.getType().name();
				List<String> blacklist = ConfigOptions.COMBAT_MOBS_BLACKLIST;
				if(blacklist.contains(enemyType)) {
					e.setCancelled(true);
					return;
				}
			}
		}

		if(tagType == TagType.PLAYER && !ConfigOptions.COMBAT_SELF) {
			LivingEntity enemy = e.getEnemy();
			UUID enemyId = enemy.getUniqueId();
			UUID playerId = player.getUniqueId();
			if(playerId.equals(enemyId)) e.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onTag(PlayerTagEvent e) {
		Player player = e.getPlayer();
		if(!ConfigOptions.COMBAT_SUDO) return;

		List<String> commandList = ConfigOptions.COMBAT_SUDO_COMMANDS;
		String playerName = player.getName();
		for(String command : commandList) {
			command = command.replace("{player}", playerName);
			if(command.startsWith("[CONSOLE]")) {
				command = command.substring(9);
				CommandSender console = Bukkit.getConsoleSender();
				try {Bukkit.dispatchCommand(console, command);}
				catch(Exception error) {error.printStackTrace();}
				continue;
			}

			if(command.startsWith("[PLAYER]")) {
				command = command.substring(8);
				try {player.performCommand(command);}
				catch(Exception error) {error.printStackTrace();}
			}

			if(command.startsWith("[OP]")) {
				command = command.substring(4);
				if(player.isOp()) {
					player.performCommand(command);
					continue;
				}

				player.setOp(true);
				try {player.performCommand(command);}
				catch(Exception error) {error.printStackTrace();}
				player.setOp(false);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onDeath(EntityDeathEvent e) {
		checkEnemyDeathUntag(e);
		checkSelfDeathUntag(e);
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onRespawn(PlayerRespawnEvent e) {
		checkSelfDeathUntag(e);
	}
	
	private void checkEnemyDeathUntag(EntityDeathEvent e) {
		if(!ConfigOptions.COMBAT_UNTAG_ON_ENEMY_DEATH) return;
		
		LivingEntity enemy = e.getEntity();
		
		OfflinePlayer offline = CombatUtil.getByEnemy(enemy);
		if(offline == null) return;

		Player player = offline.getPlayer();
		if(player == null) return;
		if(!CombatUtil.isInCombat(player)) return;
		
		CombatUtil.untag(player, UntagReason.EXPIRE_ENEMY_DEATH);
	}
	
	private void checkSelfDeathUntag(EntityDeathEvent e) {
		if(!ConfigOptions.COMBAT_UNTAG_ON_SELF_DEATH) return;
		
		LivingEntity entity = e.getEntity();
		if(!(entity instanceof Player)) return;
		
		Player player = (Player) entity;
		if(!CombatUtil.isInCombat(player)) return;
		
		CombatUtil.untag(player, UntagReason.EXPIRE);
	}
	
	private void checkSelfDeathUntag(PlayerRespawnEvent e) {
		if(!ConfigOptions.COMBAT_UNTAG_ON_SELF_DEATH) return;
		
		Player player = e.getPlayer();
		if(!CombatUtil.isInCombat(player)) return;
		
		CombatUtil.untag(player, UntagReason.EXPIRE);
	}
}