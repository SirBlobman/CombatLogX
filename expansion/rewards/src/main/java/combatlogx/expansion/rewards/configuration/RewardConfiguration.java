package combatlogx.expansion.rewards.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.IConfigurable;

import combatlogx.expansion.rewards.RewardExpansion;

public final class RewardConfiguration implements IConfigurable {
    private final RewardExpansion expansion;
    private final Map<String, Reward> rewardMap;

    private boolean usePlaceholderAPI;

    private transient Boolean placeholderApiPlugin;

    public RewardConfiguration(@NotNull RewardExpansion expansion) {
        this.expansion = expansion;
        this.rewardMap = new HashMap<>();
        this.usePlaceholderAPI = false;

        this.placeholderApiPlugin = null;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        ConfigurationSection sectionHooks = getOrCreateSection(config, "hooks");
        setUsePlaceholderAPI(sectionHooks.getBoolean("placeholderapi", true));

        resetRewards();
        ConfigurationSection rewardsSection = getOrCreateSection(config, "rewards");
        loadRewards(rewardsSection);
    }

    private @NotNull RewardExpansion getExpansion() {
        return this.expansion;
    }

    private void loadRewards(@NotNull ConfigurationSection section) {
        Set<String> rewardIdSet = section.getKeys(false);
        for (String rewardId : rewardIdSet) {
            ConfigurationSection rewardSection = section.getConfigurationSection(rewardId);
            if (rewardSection == null) {
                continue;
            }

            Reward reward = new Reward(expansion, rewardId);
            reward.load(rewardSection);
            addReward(reward);
        }
    }

    public boolean getUsePlaceholderAPI() {
        return this.usePlaceholderAPI;
    }

    public void setUsePlaceholderAPI(boolean usePlaceholderAPI) {
        this.usePlaceholderAPI = usePlaceholderAPI;
        this.placeholderApiPlugin = null;
    }

    public boolean isUsePlaceholderAPI() {
        if (this.placeholderApiPlugin != null) {
            return this.placeholderApiPlugin;
        }

        if (getUsePlaceholderAPI()) {
            PluginManager pluginManager = Bukkit.getPluginManager();
            this.placeholderApiPlugin = pluginManager.isPluginEnabled("PlaceholderAPI");
        } else {
            this.placeholderApiPlugin = false;
        }

        return this.placeholderApiPlugin;
    }

    public @NotNull Map<String, Reward> getRewardMap() {
        return Collections.unmodifiableMap(this.rewardMap);
    }

    public @NotNull Collection<Reward> getRewards() {
        Map<String, Reward> rewardMap = getRewardMap();
        Collection<Reward> rewardCollection = rewardMap.values();
        return Collections.unmodifiableCollection(rewardCollection);
    }

    public void addReward(@NotNull Reward reward) {
        String id = reward.getId();
        this.rewardMap.put(id, reward);
    }

    public void resetRewards() {
        this.rewardMap.clear();
    }
}
