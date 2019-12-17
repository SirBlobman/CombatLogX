package com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.expansion.compatibility.mvdwplaceholderapi.hook.HookMVdW;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public class CompatibilityMVdWPlaceholderAPI extends Expansion {
    public CompatibilityMVdWPlaceholderAPI(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public String getUnlocalizedName() {
        return "CompatibilityMVdWPlaceholderAPI";
    }

    @Override
    public String getName() {
        return "MVdWPlaceholderAPI Compatibility";
    }

    @Override
    public String getVersion() {
        return "15.0";
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        if(!manager.isPluginEnabled("MVdWPlaceholderAPI")) {
            logger.info("The MVdWPlaceholderAPI plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        Plugin pluginPlaceholderAPI = manager.getPlugin("MVdWPlaceholderAPI");
        if(pluginPlaceholderAPI == null) {
            logger.info("The MVdWPlaceholderAPI plugin could not be found. This expansion will be automatically disabled.");
            ExpansionManager.unloadExpansion(this);
            return;
        }

        String versionPlaceholderAPI = pluginPlaceholderAPI.getDescription().getVersion();
        logger.info("Successfully hooked into MVdWPlaceholderAPI v" + versionPlaceholderAPI);

        HookMVdW hookMVdW = new HookMVdW(this);
        hookMVdW.register();
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
}