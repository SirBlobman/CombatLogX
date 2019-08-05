package com.SirBlobman.expansion.notcombatlogx.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.expansion.notcombatlogx.NotCombatLogX;

public class ConfigNot extends Config {
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File folder = NotCombatLogX.FOLDER;
        File file = new File(folder, "not.yml");

        if (!file.exists()) copyFromJar("not.yml", folder);
        config = load(file);
    }
    
    public static boolean canDamageTypeTagPlayer(DamageCause cause) {
    	if(cause == null) return false;
    	load();
    	
    	boolean allDamage = get(config, "all damage", true);
    	if(allDamage) return true;
    	
    	String causeName = cause.name().toLowerCase().replace("_", " ");
    	String configPath = ("damage type." + causeName);
    	return get(config, configPath, false);
    }

    public static String getTagMessage(DamageCause cause) {
    	if(!canDamageTypeTagPlayer(cause)) return "";
    	
    	boolean allDamage = get(config, "all damage", true);
    	if(allDamage) return ConfigLang.getWithPrefix("messages.expansions.notcombatlogx.all damage");
    	
    	String causeName = cause.name().toLowerCase().replace("_", " ");
    	String messageKey = "messages.expansions.notcombatlogx.damage tag." + causeName;
    	return ConfigLang.getWithPrefix(messageKey);
    }
}