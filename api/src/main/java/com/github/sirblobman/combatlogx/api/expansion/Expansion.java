package com.github.sirblobman.combatlogx.api.expansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.IResourceHolder;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

public abstract class Expansion implements IResourceHolder {
    private final ICombatLogX plugin;
    private final List<Listener> listenerList;
    private State state;
    private File dataFolder, file;
    private ExpansionLogger logger;
    private ExpansionDescription description;
    private ConfigurationManager configurationManager;

    public Expansion(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
        this.listenerList = new ArrayList<>();

        this.state = State.UNLOADED;
        this.description = null;
        this.dataFolder = null;
        this.file = null;
    }

    final @NotNull List<Listener> getListeners() {
        return this.listenerList;
    }

    public final @NotNull State getState() {
        return this.state;
    }

    final void setState(@NotNull State state) {
        this.state = state;
    }

    public final @NotNull ICombatLogX getPlugin() {
        return this.plugin;
    }

    public final @NotNull Logger getLogger() {
        if (this.logger == null) {
            this.logger = new ExpansionLogger(this);
        }

        return this.logger;
    }

    protected final @NotNull ConfigurationManager getConfigurationManager() {
        if (this.configurationManager == null) {
            this.configurationManager = new ConfigurationManager(this);
        }

        return this.configurationManager;
    }

    public final @NotNull File getDataFolder() {
        return this.dataFolder;
    }

    final void setDataFolder(@NotNull File dataFolder) {
        if (!dataFolder.isDirectory()) {
            throw new IllegalArgumentException("dataFolder must be a directory!");
        }

        this.dataFolder = dataFolder;
    }

    public final @NotNull File getFile() {
        return this.file;
    }

    final void setFile(@NotNull File file) {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("file must not be a directory!");
        }

        this.file = file;
    }

    public final @NotNull ExpansionDescription getDescription() {
        return this.description;
    }

    final void setDescription(@NotNull ExpansionDescription description) {
        this.description = description;
    }

    public final @NotNull String getName() {
        ExpansionDescription description = getDescription();
        return description.getName();
    }

    public final @NotNull String getPrefix() {
        ExpansionDescription description = getDescription();
        return description.getPrefix();
    }

    @Override
    public final @NotNull String getKeyName() {
        String name = getName();
        return name.toLowerCase(Locale.US);
    }

    @Override
    public final @Nullable InputStream getResource(@NotNull String name) {
        try {
            Class<? extends Expansion> thisClass = getClass();
            URLClassLoader classLoader = (URLClassLoader) thisClass.getClassLoader();

            URL url = classLoader.findResource(name);
            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            Logger logger = getLogger();
            logger.log(Level.WARNING, "Failed to get resource '" + name + "':", ex);
            return null;
        }
    }

    protected final boolean checkDependency(@NotNull String pluginName, boolean checkEnabled) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin plugin = pluginManager.getPlugin(pluginName);
        if (plugin == null) {
            logger.warning("A dependency is not installed on the server: " + pluginName);
            return false;
        }

        if (checkEnabled && !plugin.isEnabled()) {
            logger.warning("A dependency was found but it was not enabled: " + pluginName);
            return false;
        }

        PluginDescriptionFile description = plugin.getDescription();
        String fullName = description.getFullName();
        logger.info("Successfully found a dependency: " + fullName);
        return true;
    }

    protected final boolean checkDependency(@NotNull String pluginName, boolean checkEnabled,
                                            @NotNull String versionStartsWith) {
        if (!checkDependency(pluginName, checkEnabled)) {
            return false;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        Logger logger = getLogger();

        Plugin plugin = pluginManager.getPlugin(pluginName);
        if (plugin == null) {
            return false;
        }

        PluginDescriptionFile description = plugin.getDescription();
        String version = description.getVersion();
        if (!version.startsWith(versionStartsWith)) {
            logger.info("Dependency '" + pluginName + "' is not the correct version!");
            return false;
        }

        return true;
    }

    protected final void registerListener(@NotNull Listener listener) {
        ICombatLogX plugin = getPlugin();
        JavaPlugin javaPlugin = plugin.getPlugin();
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(listener, javaPlugin);
        this.listenerList.add(listener);
    }

    protected final void selfDisable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        expansionManager.disableExpansion(this);
    }

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void reloadConfig();

    public enum State {
        LOADED, UNLOADED, ENABLED, ENABLING, DISABLED
    }
}
