package com.SirBlobman.combatlogx.expansion.compatibility.lands;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.handler.LandsNoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.lands.hook.HookLands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatibilityLands extends NoEntryExpansion {
    private NoEntryForceFieldListener forceFieldListener;
    private NoEntryHandler noEntryHandler;
    public CompatibilityLands(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("Lands");
    }

    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginLands = manager.getPlugin("Lands");
        if(pluginLands == null) {
            logger.info("Could not find the Lands plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        String versionLands = pluginLands.getDescription().getVersion();
        logger.info("Successfully hooked into Lands v" + versionLands);

        saveDefaultConfig("lands-compatibility.yml");
        HookLands hook = new HookLands(this);
        this.noEntryHandler = new LandsNoEntryHandler(this, hook);

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
        reloadConfig("lands-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}