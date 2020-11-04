package combatlogx.expansion.compatibility.citizens;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

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
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("citizens.yml");
        configurationManager.saveDefault("sentinel.yml");
    }

    @Override
    public void onEnable() {
        if(!checkDependency("Citizens", true)) {
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
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("citizens.yml");
        configurationManager.reload("sentinel.yml");
    }

    public CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public boolean isSentinelEnabled() {
        if(!this.sentinelEnabled) return false;
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enable-sentinel");
    }
}