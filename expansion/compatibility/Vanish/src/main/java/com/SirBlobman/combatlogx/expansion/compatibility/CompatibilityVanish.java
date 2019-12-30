package com.SirBlobman.combatlogx.expansion.compatibility;

import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

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

        if(manager.isPluginEnabled("SuperVanish")) {
            Plugin plugin = manager.getPlugin("SuperVanish");
            if(plugin != null) {
                String version = plugin.getDescription().getVersion();
                logger.info("Successfully hooked into SuperVanish v" + version);
            }
        }

        if(manager.isPluginEnabled("PremiumVanish")) {
            Plugin plugin = manager.getPlugin("PremiumVanish");
            if(plugin != null) {
                String version = plugin.getDescription().getVersion();
                logger.info("Successfully hooked into SuperVanish v" + version);
            }
        }

        if(manager.isPluginEnabled("Essentials")) {
            Plugin plugin = manager.getPlugin("Essentials");
            if(plugin != null) {
                String version = plugin.getDescription().getVersion();
                logger.info("Successfully hooked into Essentials v" + version);
            }
        }

        saveDefaultConfig("vanish-compatibility.yml");
    }

    @Override
    public void reloadConfig() {
        reloadConfig("vanish-compatibility.yml");
    }
}