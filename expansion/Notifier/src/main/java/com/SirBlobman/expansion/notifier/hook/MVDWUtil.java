package com.SirBlobman.expansion.notifier.hook;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import be.maximvdw.placeholderapi.EventAPI;

public class MVDWUtil extends Util {
	private static final List<UUID> hasScoreboard = newList();
	public static void enableScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if(hasScoreboard.contains(uuid)) return;
		hasScoreboard.add(uuid);

		Plugin plugin = Bukkit.getPluginManager().getPlugin("FeatherBoard");
		if(plugin != null) {
			String scoreboardName = ConfigNotifier.SCORE_BOARD_FEATHERBOARD_NAME.toLowerCase();
			EventAPI.triggerEvent(plugin, player, scoreboardName, true);
		}
	}
	
	public static void disableScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		hasScoreboard.remove(uuid);

		Plugin plugin = Bukkit.getPluginManager().getPlugin("FeatherBoard");
		if(plugin != null) {
			String scoreboardName = ConfigNotifier.SCORE_BOARD_FEATHERBOARD_NAME.toLowerCase();
			EventAPI.triggerEvent(plugin, player, scoreboardName, false);
		}
	}

	private static final List<UUID> hasAnimatedName = newList();
	public static void enableAnimatedNameTrigger(Player player) {
		UUID uuid = player.getUniqueId();
		if(hasAnimatedName.contains(uuid)) return;
		hasAnimatedName.add(uuid);

		Plugin plugin = Bukkit.getPluginManager().getPlugin("AnimatedNames");
		if(plugin != null) {
			EventAPI.triggerEvent(plugin, player, "combatlogx", true);
		}
	}
	public static void disableAnimatedNameTrigger(Player player) {
		UUID uuid = player.getUniqueId();
		hasAnimatedName.remove(uuid);

		Plugin plugin = Bukkit.getPluginManager().getPlugin("AnimatedNames");
		if(plugin != null) {
			EventAPI.triggerEvent(plugin, player, "combatlogx", false);
		}
	}
}