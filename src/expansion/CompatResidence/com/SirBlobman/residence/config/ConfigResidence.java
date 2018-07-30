package com.SirBlobman.residence.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.residence.CompatResidence;

public class ConfigResidence extends Config {
    private static final File FOLDER = CompatResidence.FOLDER;
    private static final File FILE = new File(FOLDER, "residence.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static YamlConfiguration load() {
        try {
            config = Config.load(FILE);
            defaults();
        } catch (Throwable ex) {
            String error = "Failed to load '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
        return config;
    }

    public static void save() {
        try {
            Config.save(config, FILE);
        } catch (Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }
    
    

    private static void defaults() {
        save();
    }
}