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

    private Boolean sentinelEnabled;

    public CitizensExpansion(ICombatLogX plugin) {
        super(plugin);

        this.combatNpcManager = new CombatNpcManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.sentinelEnabled = null;
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
        if (!checkDependency("Citizens", true, "2.0.30")) {
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

        this.sentinelEnabled = null;
    }

    public CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public boolean isSentinelEnabled() {
        if (this.sentinelEnabled != null) {
            return this.sentinelEnabled;
        }

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("enable-sentinel")) {
            this.sentinelEnabled = false;
            return false;
        }

        this.sentinelEnabled = checkDependency("Sentinel", true);
        return this.sentinelEnabled;
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
