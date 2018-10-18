package com.SirBlobman.combatlogx.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.SirBlobman.combatlogx.utility.Util;

public class ConfigOptions extends Config {
	private static final File FILE = new File(FOLDER, "config.yml");
	private static YamlConfiguration config = new YamlConfiguration();
	
	public static YamlConfiguration load() {
		if(!FILE.exists()) copyFromJar("config.yml", FOLDER);
		config = load(FILE);
		defaults();
		return config;
	}
	
	public static boolean OPTION_CHECK_FOR_UPDATES;
	
	public static List<String> OPTION_DISABLED_WORLDS;
	
	public static boolean OPTION_BROADCAST_ENABLE_MESSAGE;
	public static boolean OPTION_BROADCAST_DISABLE_MESSAGE;
	
	public static int OPTION_TIMER;
	
	public static boolean OPTION_LINK_PROJECTILES;
	public static boolean OPTION_LINK_PETS;
	
	
	public static boolean PUNISH_ON_KICK;
	public static boolean PUNISH_ON_QUIT;
	public static boolean PUNISH_ON_EXPIRE;
	
	public static boolean PUNISH_KILL;
	
	public static boolean PUNISH_SUDO;
	public static List<String> PUNISH_SUDO_COMMANDS;
	
	
	public static boolean COMBAT_SELF;
	
	public static boolean COMBAT_MOBS;
	public static List<String> COMBAT_MOBS_BLACKLIST;
	
	public static boolean COMBAT_SUDO;
	public static List<String> COMBAT_SUDO_COMMANDS;
	
	public static boolean COMBAT_UNTAG_ON_ENEMY_DEATH;
	public static boolean COMBAT_UNTAG_ON_SELF_DEATH;
	
	public static boolean COMBAT_BYPASS_ALLOW;
	public static String COMBAT_BYPASS_PERMISSION;
	
	private static void defaults() {
		OPTION_CHECK_FOR_UPDATES = get(config, "options.check for updates", true);
		
		OPTION_DISABLED_WORLDS = Util.toLowercaseList(get(config, "options.disabled worlds", Util.newList("world1", "world2")));

		OPTION_BROADCAST_ENABLE_MESSAGE = get(config, "options.broadcasts.on enable", true);
		OPTION_BROADCAST_ENABLE_MESSAGE = get(config, "options.broadcasts.on disable", true);
		
		OPTION_TIMER = get(config, "options.time", 30);
		
		OPTION_LINK_PROJECTILES = get(config, "options.link.projectiles", true);
		OPTION_LINK_PETS = get(config, "options.link.pets", true);
		
		
		PUNISH_ON_KICK = get(config, "punish.on kick", false);
		PUNISH_ON_QUIT = get(config, "punish.on quit", true);
		PUNISH_ON_EXPIRE = get(config, "punish.on expire", false);
		
		PUNISH_KILL = get(config, "punish.kill", true);
		
		PUNISH_SUDO = get(config, "punish.sudo.enabled", true);
		PUNISH_SUDO_COMMANDS = get(config, "punish.sudo.commands", Util.newList("[CONSOLE]eco take {player} 10000", "[PLAYER]me logged out during combat!"));
		
		COMBAT_UNTAG_ON_ENEMY_DEATH = get(config, "combat.untag on enemy death", false);
		COMBAT_UNTAG_ON_SELF_DEATH = get(config, "combat.untag on self death", true);
		
		COMBAT_SELF = get(config, "combat.self", true);
		
		COMBAT_MOBS = get(config, "combat.mobs.enabled", true);
		COMBAT_MOBS_BLACKLIST = get(config, "combat.mobs.blacklist", Util.newList(EntityType.WITHER.name(), EntityType.ARMOR_STAND.name()));
		
		COMBAT_SUDO = get(config, "combat.sudo.enabled", false);
		COMBAT_SUDO_COMMANDS = get(config, "combat.sudo.commands", Util.newList("[PLAYER]me is now in Combat"));

		COMBAT_BYPASS_ALLOW = get(config, "combat.bypass.allow", false);
		COMBAT_BYPASS_PERMISSION = get(config, "combat.bypass.permission", "combatlogx.bypass");
	}
}