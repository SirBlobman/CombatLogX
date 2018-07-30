package com.SirBlobman.expansion.notcombatlogx.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.expansion.notcombatlogx.NotCombatLogX;

public class ConfigNot extends Config {
	private static File FOLDER = NotCombatLogX.FOLDER;
	private static File FILE = new File(FOLDER, "not.yml");
	private static YamlConfiguration config = new YamlConfiguration();
	
	public static void save() {save(config, FILE);}
	public static YamlConfiguration load() {
		FOLDER = NotCombatLogX.FOLDER;
		FILE = new File(FOLDER, "not.yml");
		
		if(!FILE.exists()) copyFromJar("not.yml", FOLDER);
		config = load(FILE);
		return config;
	}
	
	public static boolean canDamageTypeTagPlayer(DamageCause dc) {
		load();
		if(dc == null) return false;
		else {
			boolean allDamage = get(config, "all damage", true);
			if(allDamage) return true;
			else {
				String name = dc.name().toLowerCase().replace("_", " ");
				String path = "damage type." + name;
				boolean enabled = get(config, path, false);
				return enabled;
			}
		}
	}
	
	public static String getTagMessage(DamageCause dc) {
		if(canDamageTypeTagPlayer(dc)) {
			boolean allDamage = get(config, "all damage", true);
			if(allDamage) {
				String msg = ConfigLang.getWithPrefix("messages.expansions.notcombatlogx.all damage");
				return msg;
			} else {
				String name = dc.name().toLowerCase().replace("_", " ");
				String path = "messages.expansions.notcombatlogx.damage tag." + name;
				String msg = ConfigLang.getWithPrefix(path);
				return msg;
			}
		} else return "";
	}
}