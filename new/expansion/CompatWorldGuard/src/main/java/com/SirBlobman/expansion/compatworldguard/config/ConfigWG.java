package com.SirBlobman.expansion.compatworldguard.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatworldguard.CompatWorldGuard;

public class ConfigWG extends Config {
	private static final File FOLDER = CompatWorldGuard.FOLDER;
	private static final File FILE = new File(FOLDER, "worldguard.yml");
	private static YamlConfiguration config = new YamlConfiguration();
	
	public static void save() {save(config, FILE);}
	public static YamlConfiguration load() {
		if(!FILE.exists()) copyFromJar("worldguard.yml", FOLDER);
		config = load(FILE);
		defaults();
		return config;
	}
	
	public static String NO_ENTRY_MODE;
	public static double NO_ENTRY_KNOCKBACK_STRENGTH;
	public static int MESSAGE_COOLDOWN;
	
	private static void defaults() {
		NO_ENTRY_MODE = get(config, "world guard.no entry.mode", "KNOCKBACK");
		NO_ENTRY_KNOCKBACK_STRENGTH = get(config, "world guard.no entry.knockback power", 1.5D);
		MESSAGE_COOLDOWN = get(config, "world guard.no entry.message cooldown", 5);
	}
	
	public static enum NoEntryMode {CANCEL, TELEPORT, KNOCKBACK, KILL};
	public static NoEntryMode getNoEntryMode() {
		if(NO_ENTRY_MODE == null || NO_ENTRY_MODE.isEmpty()) load();
		String mode = NO_ENTRY_MODE.toUpperCase();
		try {
			NoEntryMode nem = NoEntryMode.valueOf(mode);
			return nem;
		} catch(Throwable ex) {
			String error = "Invalid Mode '" + NO_ENTRY_MODE + "' in 'worldguard.yml'. Valid modes are CANCEL, TELEPORT, KNOCKBACK, or KILL";
			Util.print(error);
			return NoEntryMode.CANCEL;
		}
	}
}