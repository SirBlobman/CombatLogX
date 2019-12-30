package com.SirBlobman.combatlogx.expansion.compatibility.worldguard;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.handler.WorldGuardNoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener.ListenerWorldGuard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityWorldGuard extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityWorldGuard(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityWorldGuard";
    }

    @Override
    public String getName() {
        return "WorldGuard Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
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
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        Plugin pluginWorldGuard = manager.getPlugin("WorldGuard");
        if(pluginWorldGuard == null) {
            logger.info("The WorldGuard plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String version = pluginWorldGuard.getDescription().getVersion();
        logger.info("Successfully hooked into WorldGuard v" + version);

        saveDefaultConfig("worldguard-compatibility.yml");
        this.noEntryHandler = new WorldGuardNoEntryHandler(this);

        ListenerWorldGuard listenerWorldGuard = new ListenerWorldGuard();
        manager.registerEvents(listenerWorldGuard, plugin);

        NoEntryListener listener = new NoEntryListener(this);
        manager.registerEvents(listener, plugin);

        HookWorldGuard.registerListeners(this);

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            NoEntryForceFieldListener forceFieldListener = new NoEntryForceFieldListener(this);
            manager.registerEvents(forceFieldListener, plugin);

            String versionProtocolLib = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + versionProtocolLib);
        }
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