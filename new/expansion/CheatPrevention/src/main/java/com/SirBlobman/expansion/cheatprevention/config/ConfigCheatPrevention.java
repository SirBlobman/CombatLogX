package com.SirBlobman.expansion.cheatprevention.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.CheatPrevention;

public class ConfigCheatPrevention extends Config {
	private static final File FOLDER = CheatPrevention.FOLDER;
	private static final File FILE = new File(FOLDER, "cheat prevention.yml");
	private static YamlConfiguration config = new YamlConfiguration();
	
	public static void save() {save(config, FILE);}
	public static YamlConfiguration load() {
		if(!FILE.exists()) copyFromJar("cheat prevention.yml", FOLDER);
		config = load(FILE);
		defaults();
		return config;
	}
	
	public static boolean TELEPORTATION_ALLOW_DURING_COMBAT;
	public static boolean TELEPORTATION_ALLOW_ENDER_PEARLS;
	public static boolean TELEPORTATION_ENDER_PEARLS_RESTART_TIMER;
	
	public static boolean FLIGHT_ALLOW_DURING_COMBAT;
	public static boolean FLIGHT_ALLOW_ELYTRAS;
	public static String FLIGHT_ENABLE_PERMISSION;
	
	public static boolean GAMEMODE_CHANGE_WHEN_TAGGED;
	public static String GAMEMODE_GAMEMODE;
	
	public static boolean BLOCKED_COMMANDS_IS_WHITELIST;
	public static List<String> BLOCKED_COMMANDS_LIST;
	
	private static void defaults() {
		TELEPORTATION_ALLOW_DURING_COMBAT = get(config, "teleportation.allow during combat", false);
		TELEPORTATION_ALLOW_ENDER_PEARLS = get(config, "teleportation.allow ender pearls", false);
		TELEPORTATION_ENDER_PEARLS_RESTART_TIMER = get(config, "teleportation.ender pearls restart timer", false);
		
		FLIGHT_ALLOW_DURING_COMBAT = get(config, "flight.allow during combat", false);
		FLIGHT_ALLOW_ELYTRAS = get(config, "flight.allow elytras", false);
		FLIGHT_ENABLE_PERMISSION = get(config, "flight.enable permission", "combatlogx.flight.enable");
		
		GAMEMODE_CHANGE_WHEN_TAGGED = get(config, "gamemode.change", true);
		GAMEMODE_GAMEMODE = get(config, "gamemode.gamemode", "SURVIVAL").toUpperCase();
		
		BLOCKED_COMMANDS_IS_WHITELIST = get(config, "commands.whitelist", false);
		BLOCKED_COMMANDS_LIST = get(config, "commands.commands", Util.newList("tp", "fly", "gamemode"));
	}
}