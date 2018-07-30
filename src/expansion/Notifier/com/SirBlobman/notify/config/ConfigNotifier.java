package com.SirBlobman.notify.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.notify.Notifier;

public class ConfigNotifier extends Config {
    private static final File FILE = new File(Notifier.FOLDER, "notifier.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static YamlConfiguration load() {
        try {
            config = load(FILE);
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
            save(config, FILE);
        } catch (Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }
    
    public static boolean USE_ACTION_BAR = true;
    public static boolean USE_BOSS_BAR = true;
    public static String BOSS_BAR_COLOR = "";
    public static boolean USE_SCOREBOARD = true;
    public static String SCOREBOARD_TITLE = "";
    public static List<String> SCOREBOARD_LIST = Util.newList();
    
    private static void defaults() {
        USE_ACTION_BAR = get("options.action bar", true);
        USE_BOSS_BAR = get("options.boss bar.enable", true);
        BOSS_BAR_COLOR = get("options.boss bar.color", "YELLOW");
        USE_SCOREBOARD = get("options.scoreboard.enable", true);
        SCOREBOARD_TITLE = get("options.scoreboard.title", "&2CombatLogX");
        SCOREBOARD_LIST = get("options.scoreboard.format", Util.newList("Time Left: {time_left}", "Enemy: {enemy_name}", "Enemy Health: {enemy_health}"));
        
        save();
    }

    private static <T> T get(String path, T defaultValue) {
        return get(config, path, defaultValue);
    }
}