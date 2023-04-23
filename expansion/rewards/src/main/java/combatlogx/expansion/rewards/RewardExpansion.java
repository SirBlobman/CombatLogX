package combatlogx.expansion.rewards;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.rewards.configuration.RewardConfiguration;
import combatlogx.expansion.rewards.hook.HookVault;
import combatlogx.expansion.rewards.listener.ListenerRewards;

public final class RewardExpansion extends Expansion {
    private final RewardConfiguration configuration;
    private HookVault hookVault;

    public RewardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new RewardConfiguration(this);
        this.hookVault = null;
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        if (!checkDependency("Vault", true)) {
            expansionManager.disableExpansion(this);
            return;
        }

        this.hookVault = new HookVault(this);
        if (!this.hookVault.setupEconomy()) {
            expansionManager.disableExpansion(this);
            return;
        }

        reloadConfig();
        new ListenerRewards(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public @NotNull RewardConfiguration getConfiguration() {
        return this.configuration;
    }

    public @NotNull HookVault getVaultHook() {
        return this.hookVault;
    }
}
