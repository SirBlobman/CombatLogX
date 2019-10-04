package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

/**
 * Expansion is an abstract class used by CombatLogX expansions
 *
 * @author SirBlobman
 */
public abstract class Expansion {
    private final Map<String, FileConfiguration> fileNameToConfigMap = Util.newMap();
    private final ICombatLogX plugin;
    public Expansion(ICombatLogX plugin) {
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

    public FileConfiguration getConfig(String fileName) {
        FileConfiguration newConfig = fileNameToConfigMap.getOrDefault(fileName, null);
        if(newConfig != null) return newConfig;

        reloadConfig(fileName);
        return getConfig(fileName);
    }

    public void reloadConfig(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = this.plugin.getPlugin().getResource(fileName);
        if (defConfigStream == null) return;

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        fileNameToConfigMap.put(fileName, newConfig);
    }

    public void saveConfig(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        try {
            getConfig(fileName).save(configFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDefaultConfig(String fileName) {
        File configFile = new File(getDataFolder(), fileName);
        if(configFile.exists()) return;

        InputStream inputStream = this.plugin.getPlugin().getResource(fileName);
        if(inputStream == null) {
            Logger logger = getLogger();
            logger.info("Could not find '" + fileName + "' in class path.");
            return;
        }

        try {
            Path configPath = configFile.toPath();
            Files.copy(inputStream, configPath);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "Failed to copy file '" + fileName + "' to classpath.", ex);
        }
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