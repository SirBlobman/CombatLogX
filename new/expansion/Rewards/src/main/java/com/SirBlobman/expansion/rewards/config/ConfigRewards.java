package com.SirBlobman.expansion.rewards.config;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.rewards.Rewards;

public class ConfigRewards extends Config {
    private static File FOLDER = Rewards.FOLDER;
    private static File FILE = new File(FOLDER, "rewards.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        if(FOLDER == null || FILE == null) {
            FOLDER = Rewards.FOLDER;
            FILE = new File(FOLDER, "rewards.yml");
        }
        
        if(!FILE.exists()) copyFromJar("rewards.yml", FOLDER);
        config = load(FILE);
    }
    
    private static List<Reward> REWARD_CACHE = Util.newList();
    public static List<Reward> getRewards(boolean reload) {
        if(REWARD_CACHE.isEmpty() || reload) {
            load();
            REWARD_CACHE.clear();
            if(config.isConfigurationSection("rewards")) {
                ConfigurationSection cs = config.getConfigurationSection("rewards");
                cs.getKeys(false).forEach(key -> {
                    ConfigurationSection reward = cs.getConfigurationSection(key);
                    List<String> validWorlds = reward.getStringList("worlds");
                    List<String> commands = reward.getStringList("commands");
                    Reward r = new Reward(validWorlds, commands);
                    REWARD_CACHE.add(r);
                });
            } else {
                String error = "Invalid Rewards Config! Please reset it!";
                Util.print(error);
            }
        }
        
        return REWARD_CACHE;
    }
}