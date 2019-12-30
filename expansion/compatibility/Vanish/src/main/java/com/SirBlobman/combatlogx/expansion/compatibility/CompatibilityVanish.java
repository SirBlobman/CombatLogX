package com.SirBlobman.combatlogx.expansion.compatibility;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.expansion.compatibility.listener.ListenerVanish;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CompatibilityVanish extends Expansion {
    public CompatibilityVanish(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityVanish";
    }

    @Override
    public String getName() {
        return "Vanish Compatibility";
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
    public void onEnable() {
        Logger logger = getLogger();
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = getPlugin().getPlugin();

        if(manager.isPluginEnabled("SuperVanish")) {
            Plugin pluginSuperVanish = manager.getPlugin("SuperVanish");
            if(pluginSuperVanish != null) {
                String version = pluginSuperVanish.getDescription().getVersion();
                logger.info("Successfully hooked into SuperVanish v" + version);
            }
        }

        if(manager.isPluginEnabled("PremiumVanish")) {
            Plugin pluginPremiumVanish = manager.getPlugin("PremiumVanish");
            if(pluginPremiumVanish != null) {
                String version = pluginPremiumVanish.getDescription().getVersion();
                logger.info("Successfully hooked into SuperVanish v" + version);
            }
        }

        if(manager.isPluginEnabled("Essentials")) {
            Plugin pluginEssentials = manager.getPlugin("Essentials");
            if(pluginEssentials != null) {
                String version = pluginEssentials.getDescription().getVersion();
                logger.info("Successfully hooked into Essentials v" + version);
            }
        }

        saveDefaultConfig("vanish-compatibility.yml");
        manager.registerEvents(new ListenerVanish(this), plugin);
    }

    @Override
    public void reloadConfig() {
        reloadConfig("vanish-compatibility.yml");
    }
}