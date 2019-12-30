package com.SirBlobman.expansion.compatibility.crackshot;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.expansion.compatibility.crackshot.listener.ListenerCrackShot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityCrackShot extends Expansion {
    public CompatibilityCrackShot(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityCrackShot";
    }

    @Override
    public String getName() {
        return "CrackShot Compatibility";
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
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        if(!manager.isPluginEnabled("CrackShot")) {
            logger.info("Could not find the CrackShot plugin. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        Plugin pluginCrackShot = manager.getPlugin("CrackShot");
        if(pluginCrackShot == null) {
            logger.info("Could not find the CrackShot plugin. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String versionCrackShot = pluginCrackShot.getDescription().getVersion();
        logger.info("Successfully hooked into CrackShot v" + versionCrackShot);

        ListenerCrackShot listener = new ListenerCrackShot(this);
        manager.registerEvents(listener, plugin);
    }
}