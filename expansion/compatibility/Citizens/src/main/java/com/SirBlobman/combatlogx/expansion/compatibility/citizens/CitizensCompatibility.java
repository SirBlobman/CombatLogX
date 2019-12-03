package com.SirBlobman.combatlogx.expansion.compatibility.citizens;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener.ListenerCombat;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener.ListenerCreateNPC;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener.ListenerHandleNPC;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener.ListenerPlayerLogin;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CitizensCompatibility extends Expansion {
    private boolean successfulEnable = false;
    public CitizensCompatibility(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityCitizens";
    }

    @Override
    public String getName() {
        return "Citizens Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        if(!manager.isPluginEnabled("Citizens")) {
            logger.info("The Citizens plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        Plugin citizensPlugin = manager.getPlugin("Citizens");
        if(citizensPlugin == null) {
            logger.info("The Citizens plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String citizensVersion = citizensPlugin.getDescription().getVersion();
        logger.info("Successfully found and hooked into Citizens v" + citizensVersion);

        if(manager.isPluginEnabled("Sentinel")) {
            Plugin sentinelPlugin = manager.getPlugin("Sentinel");
            if(sentinelPlugin != null) {
                String sentinelVersion = sentinelPlugin.getDescription().getVersion();
                logger.info("Successfully found and hooked into Sentinel v" + sentinelVersion);
            }
        }

        saveDefaultConfig("citizens-compatibility.yml");
        NPCManager.onEnable();

        JavaPlugin plugin = getPlugin().getPlugin();
        manager.registerEvents(new ListenerCombat(this), plugin);
        manager.registerEvents(new ListenerCreateNPC(this), plugin);
        manager.registerEvents(new ListenerHandleNPC(this), plugin);
        manager.registerEvents(new ListenerPlayerLogin(this), plugin);

        this.successfulEnable = true;
    }

    @Override
    public void onDisable() {
        if(!this.successfulEnable) return;

        NPCManager.onDisable();
    }

    @Override
    public void reloadConfig() {
        if(!this.successfulEnable) return;

        reloadConfig("citizens-compatibility.yml");
    }
}