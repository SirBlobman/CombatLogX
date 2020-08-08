package com.SirBlobman.combatlogx.expansion.compatibility.preciousstones;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.handler.PreciousStonesNoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.listener.ListenerFieldCreation;

public class CompatibilityPreciousStones extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityPreciousStones(ICombatLogX plugin) {
        super(plugin);
        this.noEntryHandler = null;
    }

    @Override
    public void reloadConfig() {
        reloadConfig("preciousstones-compatibility.yml");
    }
    
    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled("PreciousStones")) {
            Logger logger = getLogger();
            logger.info("Could not find the PreciousStones plugin. This expansion will be automatically disabled.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();
        
        Plugin pluginPreciousStones = manager.getPlugin("PreciousStones");
        if(pluginPreciousStones == null) {
            logger.info("Could not find the PreciousStones plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }
        
        String versionPreciousStones = pluginPreciousStones.getDescription().getVersion();
        logger.info("Successfully hooked into PreciousStones v" + versionPreciousStones);
        
        saveDefaultConfig("preciousstones-compatibility.yml");
        this.noEntryHandler = new PreciousStonesNoEntryHandler(this);
        
        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);
    
        ListenerFieldCreation listenerFieldCreation = new ListenerFieldCreation(this);
        expansionManager.registerListener(this, listenerFieldCreation);
        
        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            NoEntryForceFieldListener forceFieldListener = new NoEntryForceFieldListener(this);
            expansionManager.registerListener(this, forceFieldListener);
            
            String versionProtocolLib = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + versionProtocolLib);
        }
    }
    
    @Override
    public void onActualDisable() {
        // Do Nothing
    }
    
    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}
