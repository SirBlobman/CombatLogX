package com.SirBlobman.expansion.rewards.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.rewards.Rewards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ConfigRewards extends Config {
    private static File FOLDER = Rewards.FOLDER;
    private static File FILE = new File(FOLDER, "rewards.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    private static List<Reward> REWARD_CACHE = Util.newList();

    public static void load() {
        if (FOLDER == null || FILE == null) {
            FOLDER = Rewards.FOLDER;
            FILE = new File(FOLDER, "rewards.yml");
        }

        if (!FILE.exists()) copyFromJar("rewards.yml", FOLDER);
        config = load(FILE);
    }

    public static List<Reward> getRewards(boolean reload) {
        if(!reload && !REWARD_CACHE.isEmpty()) return REWARD_CACHE;
        
        load();
        REWARD_CACHE.clear();
        
        if(!config.isConfigurationSection("rewards")) {
            Util.print("Rewards config is missing 'rewards' section, please reset it!");
            return REWARD_CACHE;
        }
        
        ConfigurationSection section = config.getConfigurationSection("rewards");
        Set<String> rewardIdSet = section.getKeys(false);
        if(rewardIdSet == null || rewardIdSet.isEmpty()) {
            Util.print("You don't have any rewards in your config, please remove this expansion or reset it!");
            return REWARD_CACHE;
        }
        
        for(String rewardId : rewardIdSet) {
            if(!section.isConfigurationSection(rewardId)) {
                Util.print("Invalid reward '" + rewardId + "' in config, please fix or remove it!");
                continue;
            }
            
            ConfigurationSection rewardSection = section.getConfigurationSection(rewardId);
            List<String> validWorldList = rewardSection.isList("worlds") ? rewardSection.getStringList("worlds") : Arrays.asList("*");
            List<String> validMobTypeList = rewardSection.isList("mob types") ? rewardSection.getStringList("mob types") : Arrays.asList("*");
            List<String> commandList = rewardSection.isList("commands") ? rewardSection.getStringList("commands") : new ArrayList<>();
            Reward reward = new Reward(validWorldList, validMobTypeList, commandList);
            REWARD_CACHE.add(reward);
        }

        return REWARD_CACHE;
    }
}