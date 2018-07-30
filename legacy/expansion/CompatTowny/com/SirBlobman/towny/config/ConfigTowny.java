package com.SirBlobman.towny.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.towny.CompatTowny;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigTowny extends Config {
    private static final File FOLDER = CompatTowny.FOLDER;
    private static final File FILE = new File(FOLDER, "towny.yml");
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

    public static boolean OPTION_NO_SAFEZONE_ENTRY = true;
    public static String OPTION_NO_SAFEZONE_ENTRY_MODE = "";
    public static double OPTION_NO_SAFEZONE_ENTRY_STRENGTH = 0.0D;

    private static void defaults() {
        OPTION_NO_SAFEZONE_ENTRY = get("options.safezones.no entry", true);
        OPTION_NO_SAFEZONE_ENTRY_MODE = get("options.safezones.mode", "KNOCKBACK").toUpperCase();
        OPTION_NO_SAFEZONE_ENTRY_STRENGTH = get("options.safezones.knockback strength", 5.0D);

        save();
    }

    private static <T> T get(String path, T defaultValue) {
        return get(config, path, defaultValue);
    }
}