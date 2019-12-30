package com.SirBlobman.combatlogx.expansion.compatibility.griefprevention;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.handler.GriefPreventionNoEntryHandler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityGriefPrevention extends NoEntryExpansion {
    private NoEntryHandler noEntryHandler;
    public CompatibilityGriefPrevention(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityGriefPrevention";
    }

    @Override
    public String getName() {
        return "GriefPrevention Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public boolean canEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled("GriefPrevention");
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        Plugin pluginGriefPrevention = manager.getPlugin("GriefPrevention");
        if(pluginGriefPrevention == null) {
            logger.info("The GriefPrevention plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String version = pluginGriefPrevention.getDescription().getVersion();
        logger.info("Successfully hooked into GriefPrevention v" + version);

        saveDefaultConfig("griefprevention-compatibility.yml");
        this.noEntryHandler = new GriefPreventionNoEntryHandler(this);

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
        reloadConfig("griefprevention-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}