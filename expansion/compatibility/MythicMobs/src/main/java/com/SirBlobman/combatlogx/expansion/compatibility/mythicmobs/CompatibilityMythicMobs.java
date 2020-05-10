package com.SirBlobman.combatlogx.expansion.compatibility.mythicmobs;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.mythicmobs.listener.ListenerMythicMobs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class CompatibilityMythicMobs extends Expansion {
    public CompatibilityMythicMobs(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        saveDefaultConfig("mythicmobs-compatibility.yml");
    }
    
    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("MythicMobs")) {
            logger.info("Could not find the MythicMobs plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
    
        Plugin pluginMythicMobs = manager.getPlugin("MythicMobs");
        if(pluginMythicMobs == null) {
            logger.info("Could not find the MythicMobs plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
    
        String versionMythicMobs = pluginMythicMobs.getDescription().getVersion();
        logger.info("Successfully hooked into MythicMobs v" + versionMythicMobs);
        
        ListenerMythicMobs listenerMythicMobs = new ListenerMythicMobs(this);
        expansionManager.registerListener(this, listenerMythicMobs);
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("mythicmobs-compatibility.yml");
    }
}