package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.api.utility.Validate;
import com.SirBlobman.combatlogx.api.ICombatLogX;

public abstract class Expansion {
    public enum State {
        LOADED, UNLOADED, ENABLED, DISABLED;
    }

    private State state;
    private File dataFolder, file;
    private ExpansionDescription description;

    private final ICombatLogX plugin;
    private final ExpansionLogger logger;
    private final ExpansionConfigurationManager configurationManager;
    private final List<Listener> listenerList;
    public Expansion(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.configurationManager = new ExpansionConfigurationManager(this);
        this.logger = new ExpansionLogger(this);
        this.listenerList = new ArrayList<>();

        this.state = State.UNLOADED;
        this.description = null;
        this.dataFolder = null;
        this.file = null;
    }

    final void setDataFolder(File dataFolder) {
        Validate.notNull(dataFolder, "dataFolder must not be null!");
        if(!dataFolder.isDirectory()) throw new IllegalArgumentException("dataFolder must be a directory!");
        this.dataFolder = dataFolder;
    }

    final void setFile(File file) {
        Validate.notNull(file, "file must not be null!");
        if(file.isDirectory()) throw new IllegalArgumentException("file must not be a directory!");
        this.file = file;
    }

    final void setDescription(ExpansionDescription description) {
        this.description = Validate.notNull(description, "description must not be null!");
    }

    final List<Listener> getListeners() {
        return this.listenerList;
    }

    final void setState(State state) {
        this.state = Validate.notNull(state, "state must not be null!");
    }

    public final State getState() {
        return this.state;
    }

    public final ICombatLogX getPlugin() {
        return this.plugin;
    }

    public final Logger getLogger() {
        return this.logger;
    }

    public final ExpansionConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public final File getDataFolder() {
        return this.dataFolder;
    }

    public final File getFile() {
        return this.file;
    }

    public final ExpansionDescription getDescription() {
        return this.description;
    }

    public final String getName() {
        ExpansionDescription description = getDescription();
        return description.getUnlocalizedName();
    }

    public final String getPrefix() {
        ExpansionDescription description = getDescription();
        return description.getDisplayName();
    }

    protected final InputStream getResource(String name) {
        Validate.notEmpty(name, "name cannot be null or empty!");
        try {
            Class<? extends Expansion> thisClass = getClass();
            ClassLoader classLoader = thisClass.getClassLoader();
            return classLoader.getResourceAsStream("/" + name);
        } catch(Exception ignored) {
            return null;
        }
    }

    protected final boolean checkDependency(String pluginName, boolean checkEnabled) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin plugin = pluginManager.getPlugin(pluginName);
        if(plugin == null) {
            logger.warning("A dependency is not installed on the server: " + pluginName);
            return false;
        }

        if(checkEnabled && !plugin.isEnabled()) {
            logger.warning("A dependency was found but it was not enabled: " + pluginName);
            return false;
        }

        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();
        logger.info("Successfully found a dependency: " + fullName);
        return true;
    }

    protected final void registerListener(Listener listener) {
        ICombatLogX plugin = getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(listener, javaPlugin);
        this.listenerList.add(listener);
    }

    public abstract void onLoad();
    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void reloadConfig();
}