package com.SirBlobman.combatlog.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlog.CombatLog;
import com.SirBlobman.combatlog.utility.Util;

public class Config {
	private static final File FOLDER = CombatLog.folder;
	private static final File FILEC = new File(FOLDER, "combat.yml");
	private static final File FILEL = new File(FOLDER, "language.yml");
	private static YamlConfiguration configc = YamlConfiguration.loadConfiguration(FILEC);
	private static YamlConfiguration configl = YamlConfiguration.loadConfiguration(FILEL);
	
	public static YamlConfiguration loadC() {
		try {
			if(!FILEC.exists()) save(configc, FILEC);
			configc.load(FILEC);
			defaultsC();
			return configc;
		} catch(Throwable ex) {
			String error = "Failed to load Config:\n" + ex.getMessage();
			Util.print(error);
			return null;
		}
	}
	
	public static YamlConfiguration loadL() {
		try {
			if(!FILEL.exists()) save(configl, FILEL);
			configl.load(FILEL);
			defaultsL();
			return configl;
		} catch(Throwable ex) {
			String error = "Failed to load Language File:\n" + ex.getMessage();
			Util.print(error);
			return null;
		}
	}
	
	public static void reload() {
		loadL();
		loadC();
	}
	
	public static void save(YamlConfiguration config, File file) {
		try {
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			config.save(file);
		} catch(Throwable ex) {
			String error = "Failed to save " + file + ":\n" + ex.getMessage();
			Util.print(error);
		}
	}
	
	/*Start Options*/
	public static int TIMER;
	public static int ENDER_PEARL_COOLDOWN;
	
	public static boolean CHECK_UPDATES;
	public static boolean ACTION_BAR;
	public static boolean BOSS_BAR;
	public static boolean SELF_COMBAT;
	public static boolean MOBS_COMBAT;
	public static boolean REMOVE_POTIONS;
	public static boolean PREVENT_FLIGHT;
	public static boolean CHANGE_GAMEMODE;
	public static boolean PUNISH_LOGGERS;
	public static boolean SUDO_LOGGERS;
	public static boolean SUDO_ON_COMBAT;
	public static boolean OPEN_INVENTORY;
	public static boolean KILL_PLAYER;
	public static boolean ENABLE_BYPASS;
	public static boolean SCOREBOARD;
	public static boolean TITLE_MANAGER = false;
	public static boolean WORLD_GUARD = false;
	
	public static List<String> DISABLED_WORLDS;
	public static List<String> BLOCKED_COMMANDS;
	public static List<String> PUNISH_COMMANDS;
	public static List<String> SUDO_COMMANDS;
	public static List<String> COMBAT_COMMANDS;
	public static List<String> BANNED_POTIONS;
	
	private static void defaultsC() {
		set(configc, "timer", 30, false);
		set(configc, "ender pearl cooldown", -1, false);
		
		set(configc, "update checker", true, false);
		set(configc, "action bar", true, false);
		set(configc, "boss bar", true, false);
		set(configc, "self combat", true, false);
		set(configc, "mobs combat", true, false);
		set(configc, "remove potions", true, false);
		set(configc, "prevent flight", true, false);
		set(configc, "change gamemode", true, false);
		set(configc, "punish loggers", true, false);
		set(configc, "prevent inventory", false, false);
		set(configc, "kill player", true, false);
		set(configc, "enable bypass", true, false);
		set(configc, "sudo loggers", false, false);
		set(configc, "sudo on combat", false, false);
		set(configc, "scoreboard", true, false);
		
		List<String> worlds = Util.newList("Creative", "WoRlD");
		List<String> commands = Util.newList("home", "tpa", "spawn");
		List<String> potions = Util.newList("INVISIBILITY", "UNLUCK");
		List<String> punish = Util.newList("ban {player} You left during combat");
		List<String> sudo = Util.newList("say I logged out of combat!");
		List<String> sudo2 = Util.newList("say I am now in combat");
		set(configc, "disabled worlds", worlds, false);
		set(configc, "blocked commands", commands, false);
		set(configc, "banned potions", potions, false);
		set(configc, "punish commands", punish, false);
		set(configc, "sudo commands", sudo, false);
		set(configc, "combat commands", sudo2, false);
		save(configc, FILEC);
		
		TIMER = configc.getInt("timer");
		ENDER_PEARL_COOLDOWN = configc.getInt("ender pearl cooldown");
		
		CHECK_UPDATES = configc.getBoolean("update checker");
		ACTION_BAR = configc.getBoolean("action bar");
		BOSS_BAR = configc.getBoolean("boss bar");
		SELF_COMBAT = configc.getBoolean("self combat");
		MOBS_COMBAT = configc.getBoolean("mobs combat");
		REMOVE_POTIONS = configc.getBoolean("remove potions");
		PREVENT_FLIGHT = configc.getBoolean("prevent flight");
		CHANGE_GAMEMODE = configc.getBoolean("change gamemode");
		PUNISH_LOGGERS = configc.getBoolean("punish loggers");
		OPEN_INVENTORY = configc.getBoolean("prevent inventory");
		KILL_PLAYER = configc.getBoolean("kill player");
		ENABLE_BYPASS = configc.getBoolean("enable bypass");
		SUDO_LOGGERS = configc.getBoolean("sudo loggers");
		SUDO_ON_COMBAT = configc.getBoolean("sudo on combat");
		SCOREBOARD = configc.getBoolean("scoreboard");
		
		DISABLED_WORLDS = configc.getStringList("disabled worlds");
		BLOCKED_COMMANDS = configc.getStringList("blocked commands");
		BANNED_POTIONS = configc.getStringList("banned potions");
		PUNISH_COMMANDS = configc.getStringList("punish commands");
		COMBAT_COMMANDS = configc.getStringList("combat commands");
		SUDO_COMMANDS = configc.getStringList("sudo commands");
	}
	/*Start Language*/
	public static String MSG_PREFIX;
	public static String MSG_TARGET;
	public static String MSG_ATTACK;
	public static String MSG_TARGET_MOB;
	public static String MSG_ATTACK_MOB;
	public static String MSG_EXPIRE;
	public static String MSG_BLOCKED;
	public static String MSG_IN_COMBAT;
	public static String MSG_NOT_IN_COMBAT;
	public static String MSG_ENDER_PEARL_COOLDOWN;
	public static String MSG_QUIT;
	public static String MSG_ACTION_BAR;
	public static String MSG_BOSS_BAR;
	public static String MSG_RELOAD_CONFIG;
	public static String MSG_INVENTORY;
	public static String SCOREBOARD_TITLE;
	public static List<String> SCOREBOARD_LIST;
	
	private static void defaultsL() {
		set(configl, "prefix", "&e[&fCombatLog&e] &f", false);
		set(configl, "target", "&5%1s&f attacked you! You are now in combat!", false);
		set(configl, "attack", "You attacked &5%1s&r! You are now in combat!", false);
		set(configl, "target mob", "You were attacked by a mob named &5%1s&f! You are now in combat!", false);
		set(configl, "attack mob", "You attacked a mob named &5%1s&f! You are now in combat!", false);
		set(configl, "expire", "You are no longer in combat!", false);
		set(configl, "blocked", "&6You can't do &c/%1s&6 during combat!", false);
		set(configl, "in combat", "You are still in combat for &b%1s&f seconds!", false);
		set(configl, "not in combat", "You are not in combat", false);
		set(configl, "enderpearl cooldown", "&cPlease wait &d%1s&c seconds before using that item!", false);
		set(configl, "quit", "&5%s&r left during combat", false);
		set(configl, "action bar", "&3Combat >> &2%1s seconds &eleft", false);
		set(configl, "boss bar", "&3Combat >> &2%1s seconds", false);
		set(configl, "reload config", "The config was reloaded", false);
		set(configl, "open inventory", "You cannot open storage blocks during combat", false);
		set(configl, "scoreboard.title", "&2Combat Log", false);
		set(configl, "scoreboard.list", Util.newList("Time Left: {time_left}", "Enemy: {enemy_name}", "Enemy Health: {enemy_health}"), false);
		save(configl, FILEL);
		
		MSG_PREFIX = configl.getString("prefix");
		MSG_TARGET = configl.getString("target");
		MSG_ATTACK = configl.getString("attack");
		MSG_TARGET_MOB = configl.getString("target mob");
		MSG_ATTACK_MOB = configl.getString("attack mob");
		MSG_EXPIRE = configl.getString("expire");
		MSG_BLOCKED = configl.getString("blocked");
		MSG_IN_COMBAT = configl.getString("in combat");
		MSG_NOT_IN_COMBAT = configl.getString("not in combat");
		MSG_ENDER_PEARL_COOLDOWN = configl.getString("enderpearl cooldown");
		MSG_QUIT = configl.getString("quit");
		MSG_ACTION_BAR = configl.getString("action bar");
		MSG_BOSS_BAR = configl.getString("boss bar");
		MSG_INVENTORY = configl.getString("open inventory");
		MSG_RELOAD_CONFIG = configl.getString("reload config");
		SCOREBOARD_TITLE = configl.getString("scoreboard.title");
		SCOREBOARD_LIST = configl.getStringList("scoreboard.list");
	}
	
	private static void set(YamlConfiguration config, String path, Object value, boolean force) {
		Object o = config.get(path);
		if(o == null || force) {
			config.set(path, value);
		}
	}
}