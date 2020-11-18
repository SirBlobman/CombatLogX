package com.SirBlobman.combatlogx.expansion.compatibility.towny;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.towny.handler.TownyNoEntryHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatibilityTowny extends NoEntryExpansion {
    private NoEntryForceFieldListener forceFieldListener;
    private NoEntryHandler noEntryHandler;
    public CompatibilityTowny(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("Towny");
    }

    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginTowny = manager.getPlugin("Towny");
        if(pluginTowny == null) {
            logger.info("Could not find the Towny plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        String versionTowny = pluginTowny.getDescription().getVersion();
        logger.info("Successfully hooked into Towny v" + versionTowny);

        saveDefaultConfig("towny-compatibility.yml");
        this.noEntryHandler = new TownyNoEntryHandler(this);

        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            forceFieldListener = new NoEntryForceFieldListener(this);
            expansionManager.registerListener(this, forceFieldListener);

            String versionProtocolLib = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + versionProtocolLib);
        }
    }
    
    @Override
    public void onActualDisable() {
        if(forceFieldListener != null)
            forceFieldListener.unregisterProtocol();
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("towny-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}