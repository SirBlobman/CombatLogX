package combatlogx.expansion.compatibility.citizens;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;

public final class CitizensExpansion extends Expansion {
    private boolean sentinelEnabled;
    private final CombatNpcManager combatNpcManager;
    public CitizensExpansion(ICombatLogX plugin) {
        super(plugin);
        this.sentinelEnabled = false;
        this.combatNpcManager = new CombatNpcManager(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("citizens.yml");
        configurationManager.saveDefault("sentinel.yml");
    }

    @Override
    public void onEnable() {
        if(!checkDependency("Citizens", true, "2.0.27")) {
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        this.sentinelEnabled = checkDependency("Sentinel", true);
    }

    @Override
    public void onDisable() {
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        combatNpcManager.removeAll();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("citizens.yml");
        configurationManager.reload("sentinel.yml");
    }

    public CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public boolean isSentinelEnabled() {
        if(!this.sentinelEnabled) return false;

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enable-sentinel");
    }
}