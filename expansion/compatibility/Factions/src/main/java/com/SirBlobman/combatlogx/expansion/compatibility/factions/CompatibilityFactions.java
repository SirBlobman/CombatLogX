package com.SirBlobman.combatlogx.expansion.compatibility.factions;

import java.util.logging.Logger;

import com.SirBlobman.api.hook.factions.FactionsHandler;
import com.SirBlobman.api.hook.factions.HookFactions;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.handler.FactionsNoEntryHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityFactions extends NoEntryExpansion {
    private FactionsNoEntryHandler noEntryHandler;
    private HookFactions<?, ?> hookFactions;
    public CompatibilityFactions(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean canEnable() {
        try {
            ICombatLogX plugin = getPlugin();
            JavaPlugin javaPlugin = plugin.getPlugin();
            FactionsHandler<?> factionsHandler = new FactionsHandler<>(javaPlugin);
            
            this.hookFactions = factionsHandler.getFactionsHook();
            return (this.hookFactions != null);
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
            NoEntryForceFieldListener forceFieldListener = new NoEntryForceFieldListener(this);
            expansionManager.registerListener(this, forceFieldListener);

            String version = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + version);
        }
    }
    
    @Override
    public void onActualDisable() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        reloadConfig("factions-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
    
    public HookFactions<?, ?> getHookFactions() {
        return this.hookFactions;
    }
}