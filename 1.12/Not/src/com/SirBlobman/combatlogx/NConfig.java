package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class NConfig extends Config {
	private static final File FILE = new File(FOLDER, "not.yml");
	private static YamlConfiguration config = new YamlConfiguration();
	
	public static YamlConfiguration load() {
		try {
			if(!FILE.exists()) save();
			config = load(FILE);
			defaults();
			return config;
		} catch(Throwable ex) {
			String error = "Failed to load NotCombatLogX Config:\n" + ex.getMessage();
			Util.print(error);
			return null;
		}
	}
	
	public static void save() {
		try {
			if(!FILE.exists()) {
				FOLDER.mkdirs();
				FILE.createNewFile();
			}
			config.save(FILE);
		} catch(Throwable ex) {
			String error = "Failed to save " + FILE + ":\n" + ex.getMessage();
			Util.print(error);
			ex.printStackTrace();
		}
	}
	
	public static boolean DROWNING = true;
	public static boolean EXPLOSION = true;
	public static boolean LAVA = true;
	public static boolean FALL = true;
	public static boolean PROJECTILE = true;
	public static boolean ALL_DAMAGE = true;
	
	public static String MSG_DROWNING = "";
	public static String MSG_EXPLOSION = "";
	public static String MSG_LAVA = "";
	public static String MSG_FALL = "";
	public static String MSG_PROJECTILE = "";
	public static String MSG_UNKNOWN = "";
	
	private static void defaults() {
		set("triggers.drowning", true, false);
		set("triggers.block explosion", true, false);
		set("triggers.lava", true, false);
		set("triggers.fall", true, false);
		set("triggers.projectile", true, false);
		set("triggers.all damage", true, false);
		
		set("messages.drowning", "You are drowning! Do not log out.", false);
		set("messages.block explosion", "You suffered explosion damage! Do not log out.", false);
		set("messages.lava", "You are melting in lava! Do not log out.", false);
		set("messages.fall", "You fell down! Do not log out.", false);
		set("messages.projectile", "You were shot by a projectile! Do not log out.", false);
		set("messages.unknown", "You took damage! Do not log out.", false);
		save();

		DROWNING = config.getBoolean("triggers.drowning");
		EXPLOSION = config.getBoolean("triggers.block explosion");
		LAVA = config.getBoolean("triggers.lava");
		FALL = config.getBoolean("triggers.fall");
		PROJECTILE = config.getBoolean("triggers.projectile");
		ALL_DAMAGE = config.getBoolean("triggers.all damage", true);
		
		MSG_DROWNING = config.getString("messages.drowning");
		MSG_EXPLOSION = config.getString("messages.block explosion");
		MSG_LAVA = config.getString("messages.lava");
		MSG_FALL = config.getString("messages.fall");
		MSG_PROJECTILE = config.getString("messages.projectile");
		MSG_UNKNOWN = config.getString("messages.unknown");
	}
	
	private static void set(String path, Object value, boolean force) {
		Object o = config.get(path);
		if(o == null || force) {
			config.set(path, value);
		}
	}
}