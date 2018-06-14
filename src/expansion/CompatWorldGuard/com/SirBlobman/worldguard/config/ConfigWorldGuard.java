package com.SirBlobman.worldguard.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.worldguard.CompatWorldGuard;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigWorldGuard extends Config {
    private static final File FOLDER = CompatWorldGuard.FOLDER;
    private static final File FILE = new File(FOLDER, "world guard.yml");
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

    public static String OPTION_FORCEFIELD_MATERIAL = "";
    public static int OPTION_FORCEFIELD_SIZE = 5;
    public static boolean OPTION_FORCEFIELD_ENABLED = true;
    public static boolean OPTION_NO_SAFEZONE_ENTRY = true;
    public static String OPTION_NO_SAFEZONE_ENTRY_MODE = "";
    public static double OPTION_NO_SAFEZONE_ENTRY_STRENGTH = 0.0D;

    private static void defaults() {
        OPTION_FORCEFIELD_MATERIAL = get("options.forcefield.material", "STAINED_GLASS:14");
        OPTION_FORCEFIELD_SIZE = get("options.forcefield.size", 5);
        OPTION_FORCEFIELD_ENABLED = get("options.forcefield.enabled", true);
        OPTION_NO_SAFEZONE_ENTRY = get("options.safezones.no entry", true);
        OPTION_NO_SAFEZONE_ENTRY_MODE = get("options.safezones.mode", "KNOCKBACK").toUpperCase();
        OPTION_NO_SAFEZONE_ENTRY_STRENGTH = get("options.safezones.knockback strength", 5.0D);
        save();
    }

    private static <T> T get(String path, T defaultValue) {
        return get(config, path, defaultValue);
    }
}