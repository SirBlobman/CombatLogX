package com.SirBlobman.expansion.rewards.config;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.rewards.Rewards;

public class ConfigRewards extends Config {
    private static File FOLDER = Rewards.FOLDER;
    private static File FILE = new File(FOLDER, "rewards.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static void save() {save(config, FILE);}
    public static YamlConfiguration load() {
        if(FOLDER == null) FOLDER = Rewards.FOLDER;
        if(FILE == null) FILE = new File(FOLDER, "notifier.yml");
        
        if(!FILE.exists()) copyFromJar("rewards.yml", FOLDER);
        config = load(FILE);
        return config;
    }
    
    public static List<Reward> REWARD_CACHE = Util.newList();
    public static List<Reward> getRewards(boolean reload) {
        if(REWARD_CACHE.isEmpty() || reload) {
            load();
            ConfigurationSection cs = config.getConfigurationSection("rewards");
            Set<String> keys = cs.getKeys(false);
            keys.forEach(key -> {
                ConfigurationSection reward = cs.getConfigurationSection(key);
                List<String> validWorlds = reward.getStringList("worlds");
                List<String> commands = reward.getStringList("commands");
                Reward r = new Reward(validWorlds, commands);
                REWARD_CACHE.add(r);
            });
        }
        
        return REWARD_CACHE;
    }
}