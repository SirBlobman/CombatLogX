package com.SirBlobman.expansion.compatfactions.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatfactions.CompatFactions;

public class ConfigFactions extends Config {
	private static File FOLDER = null;
	private static File FILE = null;
	private static YamlConfiguration config = new YamlConfiguration();

	public static YamlConfiguration load() {
		if(FOLDER == null) FOLDER = CompatFactions.FOLDER;
		if(FILE == null) FILE = new File(FOLDER, "factions.yml");
		
		if(!FILE.exists()) copyFromJar("factions.yml", FOLDER);
		config = load(FILE);
		defaults();
		return config;
	}
	
	private static String NO_ENTRY_MODE;
	public static double NO_ENTRY_KNOCKBACK_STRENGTH;
	public static int MESSAGE_COOLDOWN;
	
	private static void defaults() {
		NO_ENTRY_MODE = get(config, "factions.no entry.mode", "KNOCKBACK");
		NO_ENTRY_KNOCKBACK_STRENGTH = get(config, "factions.no entry.knockback power", 1.5D);
		MESSAGE_COOLDOWN = get(config, "factions.no entry.message cooldown", 5);
	}
	
	public enum NoEntryMode {CANCEL, TELEPORT, KNOCKBACK, KILL}

	public static NoEntryMode getNoEntryMode() {
		if(NO_ENTRY_MODE == null || NO_ENTRY_MODE.isEmpty()) load();
		String mode = NO_ENTRY_MODE.toUpperCase();
		try {
			return NoEntryMode.valueOf(mode);
		} catch(Throwable ex) {
			String error = "Invalid Mode '" + NO_ENTRY_MODE + "' in 'factions.yml'. Valid modes are CANCEL, TELEPORT, KNOCKBACK, or KILL";
			Util.print(error);
			return NoEntryMode.CANCEL;
		}
	}
}