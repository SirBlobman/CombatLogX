package combatlogx.expansion.compatibility.citizens;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.configuration.Configuration;
import combatlogx.expansion.compatibility.citizens.configuration.SentinelConfiguration;
import combatlogx.expansion.compatibility.citizens.listener.ListenerCombat;
import combatlogx.expansion.compatibility.citizens.listener.ListenerDeath;
import combatlogx.expansion.compatibility.citizens.listener.ListenerJoin;
import combatlogx.expansion.compatibility.citizens.listener.ListenerPunish;
import combatlogx.expansion.compatibility.citizens.listener.ListenerQuit;
import combatlogx.expansion.compatibility.citizens.listener.ListenerResurrect;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;

public final class CitizensExpansion extends Expansion {
    private final Configuration configuration;
    private final CitizensConfiguration citizensConfiguration;
    private final SentinelConfiguration sentinelConfiguration;
    private final CombatNpcManager combatNpcManager;
    private final InventoryManager inventoryManager;

    public CitizensExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new Configuration();
        this.citizensConfiguration = new CitizensConfiguration(this);
        this.sentinelConfiguration = new SentinelConfiguration(this);

        this.combatNpcManager = new CombatNpcManager(this);
        this.inventoryManager = new InventoryManager(this);
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
        if (!checkDependency("Citizens", true)) {
            selfDisable();
            return;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin citizens = pluginManager.getPlugin("Citizens");
        String citizensVersion = citizens.getDescription().getVersion();
        if (!citizensVersion.startsWith("2.0.30") && !citizensVersion.startsWith("2.0.31")) {
            getLogger().info("Dependency 'Citizens' is not the correct version!");
            selfDisable();
            return;
        }

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

        getConfiguration().load(configurationManager.get("config.yml"));
        getCitizensConfiguration().load(configurationManager.get("citizens.yml"));
        getSentinelConfiguration().load(configurationManager.get("sentinel.yml"));
    }

    public @NotNull CombatNpcManager getCombatNpcManager() {
        return this.combatNpcManager;
    }

    public @NotNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public boolean isSentinelEnabled() {
        Configuration configuration = getConfiguration();
        SentinelConfiguration sentinelConfiguration = getSentinelConfiguration();
        return (configuration.isEnableSentinel() && sentinelConfiguration.isSentinelPluginEnabled());
    }

    private void registerListeners() {
        new ListenerCombat(this).register();
        new ListenerDeath(this).register();
        new ListenerJoin(this).register();
        new ListenerPunish(this).register();
        new ListenerQuit(this).register();

        // Totem of Undying was added in 1.11.
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion >= 11) {
            new ListenerResurrect(this).register();
        }
    }

    public @NotNull Configuration getConfiguration() {
        return this.configuration;
    }

    public @NotNull CitizensConfiguration getCitizensConfiguration() {
        return this.citizensConfiguration;
    }

    public @NotNull SentinelConfiguration getSentinelConfiguration() {
        return this.sentinelConfiguration;
    }
}
