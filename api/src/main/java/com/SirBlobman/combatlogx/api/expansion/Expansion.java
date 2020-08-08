package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;

/**
 * Expansion is an abstract class used by CombatLogX expansions
 *
 * @author SirBlobman
 */
public abstract class Expansion {
    public enum State {
        ENABLED, DISABLED, LOADED, UNLOADED
    }
    
    private ExpansionDescription description;
    private State state = State.UNLOADED;
    private File dataFolder, file;
    
    private final ICombatLogX plugin;
    private ExpansionLogger logger;
    private final Map<String, FileConfiguration> fileNameToConfigMap = Util.newMap();
    public Expansion(ICombatLogX plugin) {
        this.plugin = plugin;
    }
    
    final void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }
    
    final void setFile(File file) {
        this.file = file;
    }
    
    final void setDescription(ExpansionDescription description) {
        this.description = description;
    }
    
    final void setState(State state) {
        this.state = state;
    }

    public final ICombatLogX getPlugin() {
        return this.plugin;
    }

    public final File getDataFolder() {
        return this.dataFolder;
    }
    
    public final File getFile() {
        return this.file;
    }
    
    public final State getState() {
        return this.state;
    }
    
    public final ExpansionDescription getDescription() {
        return this.description;
    }

    public final Logger getLogger() {
        if(this.logger != null) return this.logger;
        return (this.logger = new ExpansionLogger(this));
    }
    
    public final InputStream getResource(String fileName) {
        if(fileName == null) throw new IllegalArgumentException("fileName must not be null!");
        
        try {
            Class<? extends Expansion> expansionClass = getClass();
            ClassLoader classLoader = expansionClass.getClassLoader();
            
            URL url = classLoader.getResource(fileName);
            if(url == null) return null;
            
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch(IOException ignored) {
            return null;
        }
    }

    public final FileConfiguration getConfig(String fileName) {
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

    public final void reloadConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            String realName = realFile.getName();

            YamlConfiguration config = new YamlConfiguration();
            config.load(realFile);

            InputStream jarStream = getResource(fileName);
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

    public final void saveConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            File parentFile = realFile.getParentFile();
            if(parentFile != null && !parentFile.exists()) {
                boolean createParent = parentFile.mkdirs();
                if(!createParent) {
                    Logger logger = getLogger();
                    logger.info("Could not create parent file for '" + fileName + "'.");
                    return;
                }
            }
    
            boolean createFile = realFile.createNewFile();
            if(!createFile) {
                Logger logger = getLogger();
                logger.info("Failed to create file '" + fileName + "'.");
                return;
            }

            FileConfiguration config = getConfig(fileName);
            config.save(realFile);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error ocurred while saving a config named '" + fileName + "'.", ex);
        }
    }

    public final void saveDefaultConfig(String fileName) {
        try {
            File dataFolder = getDataFolder();
            File file = new File(dataFolder, fileName);

            File realFile = file.getCanonicalFile();
            if(realFile.exists()) return;
    
            InputStream jarStream = getResource(fileName);
            if(jarStream == null) {
                Logger logger = getLogger();
                logger.warning("Could not find file '" + fileName + "' in jar.");
                return;
            }
            
            File parentFile = realFile.getParentFile();
            if(parentFile != null && !parentFile.exists()) {
                boolean createParent = parentFile.mkdirs();
                if(!createParent) {
                    Logger logger = getLogger();
                    logger.info("Could not create parent file for '" + fileName + "'.");
                    return;
                }
            }
            
            boolean createFile = realFile.createNewFile();
            if(!createFile) {
                Logger logger = getLogger();
                logger.info("Failed to create default file '" + fileName + "'.");
                return;
            }

            Path path = realFile.toPath();
            Files.copy(jarStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch(IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.SEVERE, "An error ocurred while saving the default config for file '" + fileName + "'.", ex);
        }
    }

    public final void printHookInfo(String pluginName) {
        PluginManager manager = Bukkit.getPluginManager();
        if(!manager.isPluginEnabled(pluginName)) return;

        Plugin plugin = manager.getPlugin(pluginName);
        if(plugin == null) return;

        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();

        Logger logger = getLogger();
        logger.info("Successfully hooked into plugin '" + fullName + "'.");
    }

    public abstract void onLoad();
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void reloadConfig();
}