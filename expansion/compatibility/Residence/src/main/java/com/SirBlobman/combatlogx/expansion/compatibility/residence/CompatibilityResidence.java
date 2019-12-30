package com.SirBlobman.combatlogx.expansion.compatibility.residence;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.residence.handler.ResidenceNoEntryHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityResidence extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityResidence(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityResidence";
    }

    @Override
    public String getName() {
        return "Residence Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("Residence");
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        Plugin pluginResidence = manager.getPlugin("Residence");
        if(pluginResidence == null) {
            logger.info("Could not find the Residence plugin. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String versionResidence = pluginResidence.getDescription().getVersion();
        logger.info("Successfully hooked into Residence v" + versionResidence);

        saveDefaultConfig("residence-compatibility.yml");
        this.noEntryHandler = new ResidenceNoEntryHandler(this);

        NoEntryListener listener = new NoEntryListener(this);
        manager.registerEvents(listener, plugin);

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
        reloadConfig("residence-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}