package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.IResourceHolder;

import com.SirBlobman.combatlogx.api.ICombatLogX;

/**
 * Expansion is an abstract class used by CombatLogX expansions
 *
 * @author SirBlobman
 */
public abstract class Expansion implements IResourceHolder {
    public enum State {
        ENABLED, DISABLED, LOADED, UNLOADED
    }
    
    private final ICombatLogX plugin;
    private final ConfigurationManager configurationManager;

    private ExpansionDescription description;
    private ExpansionLogger logger;
    private File dataFolder, file;
    private State state;

    public Expansion(ICombatLogX plugin) {
        this.plugin = plugin;
        this.state = State.UNLOADED;
        this.configurationManager = new ConfigurationManager(this);
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

    @Override
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

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    @Override
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
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get(fileName);
    }

    public final void reloadConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload(fileName);
    }

    public final void saveConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.save(fileName);
    }

    public final void saveDefaultConfig(String fileName) {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault(fileName);
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
