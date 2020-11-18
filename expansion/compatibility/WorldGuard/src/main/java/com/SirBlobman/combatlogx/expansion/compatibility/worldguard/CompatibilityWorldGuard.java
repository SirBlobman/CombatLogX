package com.SirBlobman.combatlogx.expansion.compatibility.worldguard;

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
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.handler.WorldGuardNoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.ListenerWorldGuard;

public class CompatibilityWorldGuard extends NoEntryExpansion {
    private NoEntryForceFieldListener forceFieldListener;
    private NoEntryHandler noEntryHandler;
    public CompatibilityWorldGuard(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        logger.info("Registering custom WorldGuard flags...");
        HookWorldGuard.registerFlags(this);
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("WorldGuard");
    }

    @Override
    public void onActualEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        String version = pluginWorldGuard.getDescription().getVersion();
        logger.info("Successfully hooked into WorldGuard v" + version);

        saveDefaultConfig("worldguard-compatibility.yml");
        this.noEntryHandler = new WorldGuardNoEntryHandler(this);

        ListenerWorldGuard listenerWorldGuard = new ListenerWorldGuard(this);
        expansionManager.registerListener(this, listenerWorldGuard);

        NoEntryListener listener = new NoEntryListener(this);
        expansionManager.registerListener(this, listener);

        HookWorldGuard.registerListeners(this);

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
        reloadConfig("worldguard-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}