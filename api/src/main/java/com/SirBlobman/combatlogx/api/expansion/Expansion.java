package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

        String expansionName = getName();
        Logger logger = Logger.getLogger(expansionName);

        logger.setParent(parent);
        return logger;
    }

    public FileConfiguration getConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            String realName = realFile.getName();

            FileConfiguration config = fileNameToConfigMap.getOrDefault(realName, null);
            if(config != null) return config;

            reloadConfig(fileName);
            return getConfig(fileName);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error occurred while getting a config named '" + fileName + "'. An empty config will be returned.", ex);
            return new YamlConfiguration();
        }
    }

    public void reloadConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            String realName = realFile.getName();

            YamlConfiguration config = new YamlConfiguration();
            config.load(realFile);

            InputStream jarStream = this.plugin.getPlugin().getResource(fileName);
            if(jarStream != null) {
                InputStreamReader reader = new InputStreamReader(jarStream, StandardCharsets.UTF_8);
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
                config.setDefaults(defaultConfig);
            }

            fileNameToConfigMap.put(realName, config);
        } catch(IOException | InvalidConfigurationException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error ocurred while loading a config named '" + fileName + "'.", ex);
        }
    }

    public void saveConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();

            FileConfiguration config = getConfig(fileName);
            config.save(realFile);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error ocurred while saving a config named '" + fileName + "'.", ex);
        }
    }

    public void saveDefaultConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            if(realFile.exists()) return;

            InputStream jarStream = this.plugin.getPlugin().getResource(fileName);
            if(jarStream == null) {
                Logger logger = getLogger();
                logger.warning("Could not find file '" + fileName + "' in jar.");
                return;
            }

            Path path = realFile.toPath();
            Files.copy(jarStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error ocurred while saving the default config for file '" + fileName + "'.", ex);
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