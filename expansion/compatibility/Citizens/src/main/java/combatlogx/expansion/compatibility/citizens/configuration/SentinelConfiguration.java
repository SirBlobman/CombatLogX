package combatlogx.expansion.compatibility.citizens.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;

public final class SentinelConfiguration implements IConfigurable {
    private final CitizensExpansion expansion;

    private boolean attackFirst;

    private transient boolean sentinelPluginEnabled;

    public SentinelConfiguration(CitizensExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");

        this.attackFirst = false;
        this.sentinelPluginEnabled = false;
    }

    private CitizensExpansion getExpansion() {
        return this.expansion;
    }

    @Override
    public void load(ConfigurationSection config) {
        setAttackFirst(config.getBoolean("attack-first", false));

        CitizensExpansion expansion = getExpansion();
        Configuration configuration = expansion.getConfiguration();
        if (configuration.isEnableSentinel()) {
            PluginManager pluginManager = Bukkit.getPluginManager();
            this.sentinelPluginEnabled = pluginManager.isPluginEnabled("Sentinel");
        } else {
            this.sentinelPluginEnabled = false;
        }
    }

    public boolean isAttackFirst() {
        return this.attackFirst;
    }

    public void setAttackFirst(boolean attackFirst) {
        this.attackFirst = attackFirst;
    }

    public boolean isSentinelPluginEnabled() {
        return this.sentinelPluginEnabled;
    }
}
