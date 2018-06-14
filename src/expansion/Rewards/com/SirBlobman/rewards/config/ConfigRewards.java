package com.SirBlobman.rewards.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.rewards.Rewards;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigRewards extends Config {
    private static final File FOLDER = Rewards.FOLDER;
    private static final File FILE = new File(FOLDER, "rewards.yml");
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

    public static List<String> OPTION_KILL_COMMANDS = Util.newList();

    private static void defaults() {
        OPTION_KILL_COMMANDS = get(config, "options.kill commands",
                Util.newList("msg {killer} Nice kill!", "msg {player} Maybe next time!", "eco give {killer} 20"));

        save();
    }
}