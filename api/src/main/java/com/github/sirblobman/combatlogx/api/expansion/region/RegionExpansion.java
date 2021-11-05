package com.github.sirblobman.combatlogx.api.expansion.region;

import java.util.logging.Logger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public abstract class RegionExpansion extends Expansion {
    private boolean enabledSuccessfully;
    
    public RegionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.enabledSuccessfully = false;
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
        
        new RegionMoveListener(this).register();
        new RegionVulnerableListener(this).register();
        
        this.enabledSuccessfully = true;
        afterEnable();
    }
    
    @Override
    public final void onDisable() {
        if(!this.enabledSuccessfully) return;
        
        afterDisable();
        this.enabledSuccessfully = false;
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
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
