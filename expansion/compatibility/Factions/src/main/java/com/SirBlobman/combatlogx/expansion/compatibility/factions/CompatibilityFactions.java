package com.SirBlobman.combatlogx.expansion.compatibility.factions;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.factions.FactionsHandler;
import com.github.sirblobman.api.factions.FactionsHelper;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.handler.FactionsNoEntryHandler;

public class CompatibilityFactions extends NoEntryExpansion {
    private NoEntryForceFieldListener forceFieldListener;
    private FactionsNoEntryHandler noEntryHandler;
    private FactionsHandler factionsHandler;

    public CompatibilityFactions(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean canEnable() {
        try {
            ICombatLogX plugin = getPlugin();
            JavaPlugin javaPlugin = plugin.getPlugin();
            this.factionsHandler = new FactionsHelper(javaPlugin).getFactionsHandler();
            return (this.factionsHandler != null);
        } catch(IllegalStateException ex) {
            Logger logger = getLogger();
            logger.info("A Factions plugin could not be found. This expansion will be automatically disabled.");
            return false;
        }
    }

    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        this.noEntryHandler = new FactionsNoEntryHandler(this);
        saveDefaultConfig("factions-compatibility.yml");

        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            forceFieldListener = new NoEntryForceFieldListener(this);
            expansionManager.registerListener(this, forceFieldListener);

            String version = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + version);
        }
    }
    
    @Override
    public void onActualDisable() {
        if(forceFieldListener != null)
            forceFieldListener.unregisterProtocol();
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("factions-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
    
    public FactionsHandler getFactionsHandler() {
        return this.factionsHandler;
    }
}
