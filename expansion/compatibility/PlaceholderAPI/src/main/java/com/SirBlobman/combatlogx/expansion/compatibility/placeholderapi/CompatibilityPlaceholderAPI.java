package com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.placeholderapi.hook.HookPlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatibilityPlaceholderAPI extends Expansion {
    public CompatibilityPlaceholderAPI(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();
        
        if(!manager.isPluginEnabled("PlaceholderAPI")) {
            logger.info("The PlaceholderAPI plugin could not be found. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
        
        Plugin pluginPlaceholderAPI = manager.getPlugin("PlaceholderAPI");
        if(pluginPlaceholderAPI == null) {
            logger.info("The PlaceholderAPI plugin could not be found. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
        
        String versionPlaceholderAPI = pluginPlaceholderAPI.getDescription().getVersion();
        logger.info("Successfully hooked into PlaceholderAPI v" + versionPlaceholderAPI);

        HookPlaceholderAPI hookPlaceholderAPI = new HookPlaceholderAPI(this);
        hookPlaceholderAPI.register();
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
}