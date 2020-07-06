package com.SirBlobman.combatlogx.expansion.compatibility.skyblock;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook.SkyBlockHook;
import com.SirBlobman.combatlogx.expansion.compatibility.skyblock.listener.ListenerSkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class CompatibilitySkyBlock extends Expansion {
    public CompatibilitySkyBlock(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        // Do Nothing
    }
    
    @Override
    public void onDisable() {
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
        Logger logger = getLogger();
        
        SkyBlockHook hook = SkyBlockHook.getSkyBlockHook(this);
        if(hook == null) {
            logger.info("A SkyBlock plugin could not be detected. If you believe this is an error please contact SirBlobman.");
            logger.info("Automatically disabling...");
            expansionManager.disableExpansion(this);
            return;
        }
        
        Listener listener = new ListenerSkyBlock(hook);
        expansionManager.registerListener(this, listener);
    }
}