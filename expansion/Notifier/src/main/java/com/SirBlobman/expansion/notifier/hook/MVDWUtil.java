package com.SirBlobman.expansion.notifier.hook;

import be.maximvdw.placeholderapi.EventAPI;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class MVDWUtil extends Util {
	private static List<UUID> HAS_SCOREBOARD = newList();
	public static void enableScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		if(HAS_SCOREBOARD.contains(uuid)) return;
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("FeatherBoard");
		String scoreboardName = ConfigNotifier.SCORE_BOARD_FEATHERBOARD_NAME.toLowerCase();
		EventAPI.triggerEvent(plugin, player, scoreboardName, true);
		
		HAS_SCOREBOARD.add(uuid);
	}
	
	public static void disableScoreboard(Player player) {
		UUID uuid = player.getUniqueId();
		HAS_SCOREBOARD.remove(uuid);

		Plugin plugin = Bukkit.getPluginManager().getPlugin("FeatherBoard");
		String scoreboardName = ConfigNotifier.SCORE_BOARD_FEATHERBOARD_NAME.toLowerCase();
		EventAPI.triggerEvent(plugin, player, scoreboardName, false);
		
		/* Debugging Code
		
		try {
			Class<?> class_FeatherBoardAPI = Class.forName("be.maximvdw.featherboard.api.FeatherBoardAPI");
			Method method_resetDefaultScoreboard = class_FeatherBoardAPI.getDeclaredMethod("resetDefaultScoreboard", Player.class);
			method_resetDefaultScoreboard.invoke(null, player);
		} catch(ReflectiveOperationException ex) {
			if(ConfigOptions.OPTION_DEBUG) ex.printStackTrace();
		}
		
		*/
	}
}