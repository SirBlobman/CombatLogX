package combatlogx.expansion.rewards.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.rewards.RewardExpansion;
import combatlogx.expansion.rewards.object.Reward;
import combatlogx.expansion.rewards.requirement.EconomyRequirement;
import combatlogx.expansion.rewards.requirement.ExperienceRequirement;
import combatlogx.expansion.rewards.requirement.Requirement;

public final class RewardManager {
    private final RewardExpansion expansion;
    private final Set<Reward> rewardSet;

    public RewardManager(RewardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.rewardSet = new HashSet<>();
    }

    public Set<Reward> getRewards() {
        Set<Reward> rewardSet = new HashSet<>(this.rewardSet);
        return Collections.unmodifiableSet(rewardSet);
    }

    public void checkAll(Player player, LivingEntity enemy) {
        Set<Reward> rewardSet = getRewards();
        for (Reward reward : rewardSet) {
            reward.tryActivate(player, enemy);
        }
    }

    public void loadRewards() {
        RewardExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        this.rewardSet.clear();

        Logger logger = expansion.getLogger();
        ConfigurationSection rewards = configuration.getConfigurationSection("rewards");
        if (rewards == null) {
            logger.warning("Your config.yml is missing the 'rewards' section. " +
                    "If you don't want any rewards you should remove this expansion.");
            return;
        }

        Set<String> idSet = rewards.getKeys(false);
        for (String id : idSet) {
            ConfigurationSection rewardSection = rewards.getConfigurationSection(id);
            Reward reward = loadReward(id, rewardSection);
            if (reward != null) {
                this.rewardSet.add(reward);
            }
        }

        int rewardSetSize = this.rewardSet.size();
        logger.info("Successfully loaded " + rewardSetSize + " reward(s).");
    }

    private RewardExpansion getExpansion() {
        return this.expansion;
    }

    private Reward loadReward(String id, ConfigurationSection section) {
        try {
            if (section == null) {
                return null;
            }

            int chance = section.getInt("chance", 1);
            int maxChance = section.getInt("max-chance", 1);
            boolean randomCommand = section.getBoolean("random-command", false);
            List<String> commandList = section.getStringList("commands");
            Reward reward = new Reward(this.expansion, chance, maxChance, randomCommand, commandList);

            if (section.isBoolean("mob-whitelist")) {
                boolean whitelist = section.getBoolean("mob-whitelist");
                reward.setMobWhiteList(whitelist);
            }

            if (section.isBoolean("world-whitelist")) {
                boolean whitelist = section.getBoolean("world-whitelist");
                reward.setWorldWhiteList(whitelist);
            }

            if (section.isList("world-list")) {
                List<String> worldNameList = section.getStringList("world-list");
                reward.setWorldList(worldNameList);
            }

            if (section.isList("mob-list")) {
                List<String> mobTypeNameList = section.getStringList("mob-list");
                reward.setMobList(mobTypeNameList);
            }

            List<Requirement> requirementList = loadRequirements(section);
            if (requirementList != null) {
                reward.setRequirements(requirementList);
            }

            return reward;
        } catch (Exception ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "Failed to load reward with ID '" + id + "':", ex);
            return null;
        }
    }

    private List<Requirement> loadRequirements(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        ConfigurationSection sectionRequirements = section.getConfigurationSection("requirements");
        if (sectionRequirements == null) {
            return null;
        }

        Set<String> keySet = sectionRequirements.getKeys(false);
        List<Requirement> requirementList = new ArrayList<>();
        for (String key : keySet) {
            ConfigurationSection sectionRequirement = sectionRequirements.getConfigurationSection(key);
            Requirement requirement = loadRequirement(sectionRequirement);
            if (requirement != null) {
                requirementList.add(requirement);
            }
        }

        return requirementList;
    }

    private Requirement loadRequirement(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        boolean enemy = section.getBoolean("check-enemy");
        String type = section.getString("type");
        if (type == null) {
            return null;
        }

        switch (type) {
            case "economy":
                return loadEconomyRequirement(section, enemy);
            case "experience":
                return loadExperienceRequirement(section, enemy);
            default:
                return null;
        }
    }

    private Requirement loadEconomyRequirement(ConfigurationSection section, boolean enemy) {
        double amount = section.getDouble("amount");
        return new EconomyRequirement(this.expansion, enemy, amount);
    }

    private Requirement loadExperienceRequirement(ConfigurationSection section, boolean enemy) {
        int amount = section.getInt("amount");
        return new ExperienceRequirement(this.expansion, enemy, amount);
    }
}
