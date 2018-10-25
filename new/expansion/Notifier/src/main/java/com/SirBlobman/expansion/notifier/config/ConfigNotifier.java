package com.SirBlobman.expansion.notifier.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.Notifier;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigNotifier extends Config {
	private static YamlConfiguration config = new YamlConfiguration();

	public static void load() {
		File folder = Notifier.FOLDER;
		File file = new File(folder, "notifier.yml");
		
		if(!file.exists()) copyFromJar("notifier.yml", folder);
		config = load(file);
		defaults();
		
		if(SCORE_BOARD_USE_FEATHERBOARD && PluginUtil.isEnabled("FeatherBoard")) {
			File mainFolder = new File(".").getAbsoluteFile().getParentFile();
			File pluginsFolder = new File(mainFolder, "plugins");
			File scoreboardFile = new File(pluginsFolder, "FeatherBoard" + File.pathSeparator + "scoreboards" + File.pathSeparator + "combatlogx.yml");
			if(!scoreboardFile.exists()) copyFromJar("FeatherBoard/scoreboards/combatlogx.yml", pluginsFolder);
			Bukkit.dispatchCommand(Util.CONSOLE, "fb reload");
		}
	}

	public static boolean ACTION_BAR_ENABLED;
	public static String ACTION_BAR_FORMAT;
	public static String ACTION_BAR_NO_LONGER_IN_COMBAT;
	
	public static boolean BOSS_BAR_ENABLED;
	public static String BOSS_BAR_COLOR;
	public static String BOSS_BAR_STYLE;
	public static String BOSS_BAR_FORMAT;
	public static String BOSS_BAR_NO_LONGER_IN_COMBAT;
	
	public static boolean SCORE_BOARD_ENABLED;
	public static String SCORE_BOARD_TITLE;
	public static List<String> SCORE_BOARD_LINES;
	public static boolean SCORE_BOARD_USE_FEATHERBOARD;
	public static String SCORE_BOARD_FEATHERBOARD_NAME;
	
	private static void defaults() {
		ACTION_BAR_ENABLED = get(config, "action bar.enabled", true);
		ACTION_BAR_FORMAT = get(config, "action bar.format", "&3&lCombat &7>> {bars_left}{bars_right} &2{time_left} seconds.");
		ACTION_BAR_NO_LONGER_IN_COMBAT = get(config, "action bar.no longer in combat", "&3&lCombat &7>> &a&oYou are no longer in combat.");
		
		BOSS_BAR_ENABLED = get(config, "boss bar.enabled", true);
		BOSS_BAR_COLOR = get(config, "boss bar.color", "YELLOW");
		BOSS_BAR_STYLE = get(config, "boss bar.style", "SOLID");
		BOSS_BAR_FORMAT = get(config, "boss bar.format", "&3Combat &7>> &c{time_left} seconds");
		BOSS_BAR_NO_LONGER_IN_COMBAT = get(config, "boss bar.no longer in combat", "&3Combat &7>> &a&oYou are no longer in combat");
		
		SCORE_BOARD_ENABLED = get(config, "score board.enabled", true);
		SCORE_BOARD_TITLE = get(config, "score board.title", "&6&lCombatLogX");
		SCORE_BOARD_LINES = get(config, "score board.lines", Util.newList("&f&lTime Left: &7{time_left}", "&f&lEnemy Name: &7{enemy_name}", "&f&lEnemy Health: &7{enemy_health}"));
		SCORE_BOARD_USE_FEATHERBOARD = get(config, "score board.featherboard.use", false);
		SCORE_BOARD_FEATHERBOARD_NAME = get(config, "score board.featherboard.board", "combatlogx");
	}
}