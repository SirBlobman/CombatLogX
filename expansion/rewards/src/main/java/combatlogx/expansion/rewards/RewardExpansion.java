package combatlogx.expansion.rewards;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.rewards.hook.HookVault;
import combatlogx.expansion.rewards.listener.ListenerRewards;
import combatlogx.expansion.rewards.manager.RewardManager;

public final class RewardExpansion extends Expansion {
    private final RewardManager rewardManager;
    private HookVault hookVault;
    public RewardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.rewardManager = new RewardManager(this);
        this.hookVault = null;
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        if(!checkDependency("Vault", true)) {
            expansionManager.disableExpansion(this);
            return;
        }

        this.hookVault = new HookVault(this);
        if(!this.hookVault.setupEconomy()) {
            expansionManager.disableExpansion(this);
            return;
        }

        RewardManager rewardManager = getRewardManager();
        rewardManager.loadRewards();

        new ListenerRewards(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        RewardManager rewardManager = getRewardManager();
        rewardManager.loadRewards();
    }

    public RewardManager getRewardManager() {
        return this.rewardManager;
    }

    public HookVault getVaultHook() {
        return this.hookVault;
    }
}