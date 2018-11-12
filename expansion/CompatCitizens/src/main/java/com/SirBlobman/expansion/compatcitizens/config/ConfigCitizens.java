package com.SirBlobman.expansion.compatcitizens.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.compatcitizens.CompatCitizens;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigCitizens extends Config {
    public static boolean CANCEL_OTHER_PUNISHMENTS;
    public static String ENTITY_TYPE;
    public static boolean STORE_INVENTORY;
    public static int SURVIVAL_TIME;
    public static boolean USE_SENTINELS;
    private static File FOLDER = CompatCitizens.FOLDER;
    private static File FILE = new File(FOLDER, "citizens.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        if (FOLDER == null) FOLDER = CompatCitizens.FOLDER;
        if (FILE == null) FILE = new File(FOLDER, "citizens.yml");

        if (!FILE.exists()) copyFromJar("citizens.yml", FOLDER);
        config = load(FILE);
        defaults();
    }

    private static void defaults() {
        ENTITY_TYPE = get(config, "citizens.entity type", "PLAYER");
        STORE_INVENTORY = get(config, "citizens.store inventory", true);
        SURVIVAL_TIME = get(config, "citizens.survival time", 30);
        USE_SENTINELS = get(config, "citizens.sentinels", PluginUtil.isEnabled("Sentinel", "mcmonkey"));
        CANCEL_OTHER_PUNISHMENTS = get(config, "cancel other punishments", true);
    }
}