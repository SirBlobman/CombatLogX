package combatlogx.expansion.compatibility.citizens;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.citizens.listener.ListenerCombat;
import combatlogx.expansion.compatibility.citizens.listener.ListenerDeath;
import combatlogx.expansion.compatibility.citizens.listener.ListenerJoin;
import combatlogx.expansion.compatibility.citizens.listener.ListenerPunish;
import combatlogx.expansion.compatibility.citizens.listener.ListenerResurrect;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;

public final class CitizensExpansion extends Expansion {
    private final CombatNpcManager combatNpcManager;
    private final InventoryManager inventoryManager;

    private boolean sentinelEnabled;

    public CitizensExpansion(ICombatLogX plugin) {
        super(plugin);

        this.combatNpcManager = new CombatNpcManager(this);
        this.inventoryManager = new InventoryManager(this);

        this.sentinelEnabled = false;
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
        if (!checkDependency("Citizens", true, "2.0.29")) {
            selfDisable();
            return;
        }

        this.sentinelEnabled = checkDependency("Sentinel", true);
        registerListeners();
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

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public boolean isSentinelEnabled() {
        if (!this.sentinelEnabled) {
            return false;
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enable-sentinel");
    }

    private void registerListeners() {
        new ListenerCombat(this).register();
        new ListenerDeath(this).register();
        new ListenerJoin(this).register();
        new ListenerPunish(this).register();

        // Totem of Undying was added in 1.11.
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion >= 11) {
            new ListenerResurrect(this).register();
        }
    }
}
