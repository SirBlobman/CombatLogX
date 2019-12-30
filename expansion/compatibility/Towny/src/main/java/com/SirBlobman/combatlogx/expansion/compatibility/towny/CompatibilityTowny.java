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
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityTowny extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityTowny(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityTowny";
    }

    @Override
    public String getName() {
        return "Towny Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("Towny");
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        Plugin pluginTowny = manager.getPlugin("Towny");
        if(pluginTowny == null) {
            logger.info("Could not find the Towny plugin. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String versionTowny = pluginTowny.getDescription().getVersion();
        logger.info("Successfully hooked into Towny v" + versionTowny);

        saveDefaultConfig("towny-compatibility.yml");
        this.noEntryHandler = new TownyNoEntryHandler(this);

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
        reloadConfig("towny-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}