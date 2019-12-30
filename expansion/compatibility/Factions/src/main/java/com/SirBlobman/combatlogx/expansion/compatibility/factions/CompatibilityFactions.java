package com.SirBlobman.combatlogx.expansion.compatibility.factions;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryExpansion;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryForceFieldListener;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryHandler;
import com.SirBlobman.combatlogx.api.expansion.noentry.NoEntryListener;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.handler.FactionsNoEntryHandler;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.FactionsHook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityFactions extends NoEntryExpansion {
    private FactionsNoEntryHandler noEntryHandler;
    public CompatibilityFactions(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityFactions";
    }

    @Override
    public String getName() {
        return "Factions Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public boolean canEnable() {
        FactionsHook factionsHook = FactionsHook.getFactionsHook(this);
        return (factionsHook != null);
    }

    @Override
    public void onActualEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        FactionsHook factionsHook = FactionsHook.getFactionsHook(this);
        if(factionsHook == null) {
            logger.info("A Factions plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        this.noEntryHandler = new FactionsNoEntryHandler(this, factionsHook);
        saveDefaultConfig("factions-compatibility.yml");

        NoEntryListener listener = new NoEntryListener(this);
        manager.registerEvents(listener, plugin);

        Plugin pluginProtocolLib = manager.getPlugin("ProtocolLib");
        if(pluginProtocolLib != null) {
            NoEntryForceFieldListener forceFieldListener = new NoEntryForceFieldListener(this);
            manager.registerEvents(forceFieldListener, plugin);

            String version = pluginProtocolLib.getDescription().getVersion();
            logger.info("Successfully hooked into ProtocolLib v" + version);
        }
    }

    @Override
    public void reloadConfig() {
        reloadConfig("factions-compatibility.yml");
    }

    @Override
    public NoEntryHandler getNoEntryHandler() {
        return this.noEntryHandler;
    }
}