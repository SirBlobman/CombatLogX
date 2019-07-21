package com.SirBlobman.expansion.citizens.config;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.expansion.citizens.CompatCitizens;

import java.io.File;

public class ConfigCitizens extends Config {
    private static File FOLDER = CompatCitizens.FOLDER;
    private static File FILE = new File(FOLDER, "citizens.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        if (FOLDER == null) FOLDER = CompatCitizens.FOLDER;
        if (FILE == null) FILE = new File(FOLDER, "citizens.yml");
        if (!FILE.exists()) copyFromJar("citizens.yml", FOLDER);
        
        config = load(FILE);
    }
    
    public static <O> O getOption(String path, O defaultValue) {
        load();
        return get(config, path, defaultValue);
    }
}