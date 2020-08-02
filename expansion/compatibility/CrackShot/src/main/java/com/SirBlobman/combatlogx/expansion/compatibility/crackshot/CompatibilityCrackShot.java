package com.SirBlobman.combatlogx.expansion.compatibility.crackshot;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.crackshot.listener.ListenerCrackShot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatibilityCrackShot extends Expansion {
    public CompatibilityCrackShot(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
    
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        if(!manager.isPluginEnabled("CrackShot")) {
            logger.info("Could not find the CrackShot plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        Plugin pluginCrackShot = manager.getPlugin("CrackShot");
        if(pluginCrackShot == null) {
            logger.info("Could not find the CrackShot plugin. This expansion will be automatically disabled.");
            expansionManager.disableExpansion(this);
            return;
        }

        String versionCrackShot = pluginCrackShot.getDescription().getVersion();
        logger.info("Successfully hooked into CrackShot v" + versionCrackShot);

        ListenerCrackShot listener = new ListenerCrackShot(this);
        expansionManager.registerListener(this, listener);
    }
}