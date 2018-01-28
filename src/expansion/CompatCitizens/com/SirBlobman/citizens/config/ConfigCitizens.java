package com.SirBlobman.citizens.config;

import com.SirBlobman.citizens.CompatCitizens;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;

public class ConfigCitizens extends Config {
    private static final File FOLDER = CompatCitizens.FOLDER;
    private static final File FILE = new File(FOLDER, "config.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static YamlConfiguration load() {
        try {
            config = Config.load(FILE);
            defaults();
        } catch(Throwable ex) {
            String error = "Failed to load '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
        return config;
    }
    
    public static void save() {
        try {
            Config.save(config, FILE);
        } catch(Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }
    
    public static String OPTION_NPC_ENTITY_TYPE = EntityType.PLAYER.name();
    public static int OPTION_NPC_SURVIVAL_TIME = 0;
    
    private static void defaults() {
        OPTION_NPC_ENTITY_TYPE = get(config, "options.npc.entity type", EntityType.PLAYER.name());
        OPTION_NPC_SURVIVAL_TIME = get(config, "options.npc.survival time", 300);
        save();
    }
}