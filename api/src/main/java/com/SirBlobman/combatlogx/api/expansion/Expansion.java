package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;

/**
 * Expansion is an abstract class used by CombatLogX expansions
 *
 * @author SirBlobman
 */
public abstract class Expansion {
    private final ICombatLogX plugin;
    Expansion(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    public final ICombatLogX getPlugin() {
        return this.plugin;
    }

    public final File getDataFolder() {
        ICombatLogX plugin = getPlugin();
        File dataFolder = plugin.getDataFolder();

        File expansionsFolder = new File(dataFolder, "expansions");
        File expansionFolder = new File(expansionsFolder, getUnlocalizedName());
        expansionFolder.mkdirs();

        return expansionFolder;
    }

    public final Logger getLogger() {
        ICombatLogX plugin = getPlugin();
        Logger parent = plugin.getLogger();

        Logger logger = Logger.getLogger(getName());
        logger.setParent(parent);
        return logger;
    }

    public String getName() {
        return getUnlocalizedName();
    }

    public abstract String getUnlocalizedName();
    public abstract String getVersion();

    public abstract void onLoad();
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void reloadConfig();
}