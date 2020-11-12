package com.SirBlobman.combatlogx.api.expansion.region;

import java.util.logging.Logger;

import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class RegionExpansion extends Expansion {
    private boolean enabledSuccessfully;
    private final RegionForceField regionForceField;
    public RegionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.enabledSuccessfully = false;
        this.regionForceField = new RegionForceField(this);
    }

    @Override
    public final void onEnable() {
        ICombatLogX plugin = getPlugin();
        if(!checkDependencies()) {
            Logger logger = getLogger();
            logger.info("Some dependencies for this expansion are missing!");

            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("force-field.yml");
        if(configuration.getBoolean("enabled")) {
            this.regionForceField.registerProtocol();
            registerListener(this.regionForceField);
        }

        RegionMoveListener regionMoveListener = new RegionMoveListener(this);
        regionMoveListener.register();

        this.enabledSuccessfully = true;
        afterEnable();
    }

    @Override
    public final void onDisable() {
        if(!this.enabledSuccessfully) return;
        this.regionForceField.unregisterProtocol();

        afterDisable();
        this.enabledSuccessfully = false;
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    /**
     * This method can be overridden if you need to do something when the expansion is enabled.
     */
    public void afterEnable() {
        // Do Nothing
    }

    /**
     * This method can be overridden if you need to do something when the expansion is disabled.
     */
    public void afterDisable() {
        // Do Nothing
    }

    public abstract boolean checkDependencies();

    public abstract RegionHandler getRegionHandler();
}