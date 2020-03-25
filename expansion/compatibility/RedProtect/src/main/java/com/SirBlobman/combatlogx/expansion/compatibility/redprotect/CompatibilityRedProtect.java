package com.SirBlobman.combatlogx.expansion.compatibility.redprotect;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.redprotect.handler.RedProtectNoEntryHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatibilityRedProtect extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityRedProtect(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("RedProtect");
    }

    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();
    
    
        Plugin pluginRedProtect = manager.getPlugin("RedProtect");
        if(pluginRedProtect == null) {
            logger.info("Could not find the RedProtect plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        String versionRedProtect = pluginRedProtect.getDescription().getVersion();
        logger.info("Successfully hooked into RedProtect v" + versionRedProtect);

        saveDefaultConfig("redprotect-compatibility.yml");
        this.noEntryHandler = new RedProtectNoEntryHandler(this);

        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);

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
    public void reloadConfig() {
        reloadConfig("redprotect-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}
