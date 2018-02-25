package com.SirBlobman.worldguard.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.worldguard.CompatWorldGuard;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class WGConfig extends Config {
    private static final File FOLDER = CompatWorldGuard.FOLDER;
    private static final File FILE = new File(FOLDER, "world guard.yml");
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
    
    private static void defaults() {
        save();
    }
}