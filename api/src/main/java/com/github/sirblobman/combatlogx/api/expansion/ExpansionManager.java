package com.github.sirblobman.combatlogx.api.expansion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion.State;

public final class ExpansionManager {
    private final ICombatLogX plugin;
    private final Map<String, Expansion> expansionMap;
    private final Map<Expansion, ExpansionClassLoader> expansionClassLoaderMap;
    private final Map<String, Class<?>> classNameMap;

    public ExpansionManager(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
        this.expansionMap = new HashMap<>();
        this.expansionClassLoaderMap = new HashMap<>();
        this.classNameMap = new HashMap<>();
    }

    public @NotNull ICombatLogX getPlugin() {
        return this.plugin;
    }

    public void loadExpansions() {
        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();
        logger.info("Loading expansions...");

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logger.warning("The CombatLogX folder does not exist and could not be created!");
            return;
        }

        File expansionsFolder = new File(dataFolder, "expansions");
        if (!expansionsFolder.exists() && !expansionsFolder.mkdirs()) {
            logger.warning("The expansions folder does not exist and could not be created!");
            return;
        }

        FilenameFilter filter = (folder, fileName) -> fileName.endsWith(".jar");
        File[] fileArray = expansionsFolder.listFiles(filter);
        if (fileArray == null || fileArray.length == 0) {
            logger.info("There were no expansions to load.");
            return;
        }

        for (File file : fileArray) {
            if (file.isDirectory()) {
                continue;
            }

            loadExpansion(file);
            logger.info(" ");
        }

        List<Expansion> expansionList = sortExpansions(getLoadedExpansions());
        int expansionListSize = expansionList.size();

        String message = ("Successfully loaded " + expansionListSize + " expansion" +
                (expansionListSize == 1 ? "" : "s") + ".");
        logger.info(message);
    }

    public void enableExpansions() {
        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();
        logger.info("Enabling expansions...");

        List<Expansion> loadedExpansionList = getLoadedExpansions();
        if (loadedExpansionList.isEmpty()) {
            logger.info("There were no expansions to enable.");
            return;
        }

        sortExpansions(loadedExpansionList);
        List<Expansion> lateLoadExpansionList = new ArrayList<>();

        for (Expansion expansion : loadedExpansionList) {
            ExpansionDescription description = expansion.getDescription();
            if (description.isLateLoad()) {
                lateLoadExpansionList.add(expansion);
                continue;
            }

            enableExpansion(expansion);
            logger.info(" ");
        }

        List<Expansion> enabledExpansionList = getEnabledExpansions();
        int expansionListSize = enabledExpansionList.size();
        String message = ("Successfully enabled " + expansionListSize + " expansion"
                + (expansionListSize == 1 ? "" : "s") + ".");
        logger.info(message);

        TaskDetails<ConfigurablePlugin> task = new TaskDetails<ConfigurablePlugin>(plugin.getPlugin()) {
            @Override
            public void run() {
                for (Expansion expansion : lateLoadExpansionList) {
                    enableExpansion(expansion);
                    logger.info(" ");
                }

                List<Expansion> newEnabledExpansionList = getEnabledExpansions();
                int newExpansionListSize = newEnabledExpansionList.size();
                int newExpansionCount = (newExpansionListSize - expansionListSize);

                String newMessage = ("Successfully enabled " + newExpansionCount + " late-load expansion"
                        + (expansionListSize == 1 ? "" : "s") + ".");
                logger.info(newMessage);
            }
        };
        task.setDelay(1L);

        TaskScheduler<ConfigurablePlugin> scheduler = plugin.getFoliaHelper().getScheduler();
        scheduler.scheduleTask(task);
    }

    public void disableExpansions() {
        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();
        logger.info("Disabling expansions...");

        List<Expansion> enabledExpansionList = getEnabledExpansions();
        if (enabledExpansionList.isEmpty()) {
            logger.info("There were no expansions to disable.");
        } else {
            for (Expansion expansion : enabledExpansionList) {
                disableExpansion(expansion);
                logger.info(" ");
            }
        }

        this.expansionMap.clear();
        this.classNameMap.clear();
        this.expansionClassLoaderMap.clear();
        logger.info("Successfully disabled all expansions.");
    }

    public void reloadConfigs() {
        List<Expansion> expansionList = getEnabledExpansions();
        expansionList.forEach(Expansion::reloadConfig);
    }

    public @NotNull Optional<Expansion> getExpansion(String name) {
        if (name == null) {
            return Optional.empty();
        }

        Expansion expansion = this.expansionMap.getOrDefault(name, null);
        return Optional.ofNullable(expansion);
    }

    public @NotNull List<Expansion> getAllExpansions() {
        Collection<Expansion> expansionCollection = this.expansionMap.values();
        return new ArrayList<>(expansionCollection);
    }

    public @NotNull List<Expansion> getLoadedExpansions() {
        List<Expansion> expansionList = getAllExpansions();
        return expansionList.stream()
                .filter(expansion -> expansion.getState() == State.LOADED)
                .collect(Collectors.toList());
    }

    public @NotNull List<Expansion> getEnabledExpansions() {
        List<Expansion> expansionList = getAllExpansions();
        return expansionList.stream()
                .filter(expansion -> expansion.getState() == State.ENABLED)
                .sorted(Comparator.comparing(Expansion::getName))
                .collect(Collectors.toList());
    }

    public @Nullable ExpansionClassLoader getClassLoader(Expansion expansion) {
        return this.expansionClassLoaderMap.getOrDefault(expansion, null);
    }

    public @Nullable Class<?> getClassByName(String name) {
        try {
            Class<?> defaultValue = this.expansionClassLoaderMap.values()
                    .stream().map(loader -> loader.findClass(name, false))
                    .filter(Objects::nonNull).findFirst().orElse(null);
            return this.classNameMap.getOrDefault(name, defaultValue);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setClass(@NotNull String name, @NotNull Class<?> clazz) {
        this.classNameMap.putIfAbsent(name, clazz);
    }

    private void loadExpansion(@NotNull File expansionFile) {
        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();
        plugin.printDebug("Attempting to load expansion from file '" + expansionFile + "'...");

        Expansion expansion;
        ExpansionClassLoader expansionClassLoader;
        try (JarFile jarFile = new JarFile(expansionFile)) {
            YamlConfiguration description = getExpansionDescription(jarFile);
            Class<? extends ExpansionManager> managerClass = getClass();
            ClassLoader managerClassLoader = managerClass.getClassLoader();

            PluginManager pluginManager = Bukkit.getPluginManager();
            if (description.isList("plugin-depend")) {
                List<String> pluginDependList = description.getStringList("plugin-depend");
                for (String pluginName : pluginDependList) {
                    Plugin dependencyPlugin = pluginManager.getPlugin(pluginName);
                    if (dependencyPlugin != null) {
                        continue;
                    }

                    logger.warning("Failed to load expansion '" + expansionFile + "' because a plugin " +
                            "dependency was not loaded: " + pluginName);
                    return;
                }
            }

            if (description.isList("expansion-depend")) {
                List<String> expansionDependList = description.getStringList("expansion-depend");
                for (String expansionName : expansionDependList) {
                    if (this.expansionMap.containsKey(expansionName)) {
                        continue;
                    }

                    logger.warning("Failed to load expansion '" + expansionFile + "' because an expansion" +
                            " dependency was missing: " + expansionName);
                    return;
                }
            }

            expansionClassLoader = new ExpansionClassLoader(this, description, expansionFile,
                    managerClassLoader);
            expansion = expansionClassLoader.getExpansion();
        } catch (Exception ex) {
            logger.warning("An expansion failed to load because an error occurred.");
            logger.warning("If debug-mode is enabled, the full error will be displayed below.");
            this.plugin.printDebug(ex);
            return;
        }

        File pluginFolder = plugin.getDataFolder();
        File expansionsFolder = new File(pluginFolder, "expansions");
        String expansionName = expansion.getName();
        File dataFolder = new File(expansionsFolder, expansionName);
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logger.warning("Failed to create folder for expansion '" + expansionName + "'.");
            return;
        }

        expansion.setFile(expansionFile);
        expansion.setDataFolder(dataFolder);

        this.expansionMap.put(expansionName, expansion);
        this.expansionClassLoaderMap.put(expansion, expansionClassLoader);

        try {
            ExpansionDescription description = expansion.getDescription();
            String fullName = description.getFullName();
            logger.info("Loading expansion '" + fullName + "'...");

            expansion.onLoad();
            expansion.setState(State.LOADED);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error occurred while loading an expansion:", ex);
            logger.warning("Failed to load expansion from file '" + expansionFile + "'.");
        }
    }

    public void enableExpansion(@NotNull Expansion expansion) {
        State state = expansion.getState();
        if (state == State.ENABLED) {
            return;
        }

        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();

        try {
            ExpansionDescription description = expansion.getDescription();
            String fullName = description.getFullName();
            logger.info("Enabling expansion '" + fullName + "'...");

            expansion.setState(State.ENABLED);
            expansion.onEnable();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error occurred while enabling an expansion:", ex);
        }
    }

    public void disableExpansion(@NotNull Expansion expansion) {
        State state = expansion.getState();
        if (state != State.ENABLED) {
            return;
        }

        ICombatLogX plugin = getPlugin();
        Logger logger = plugin.getLogger();

        try {
            ExpansionDescription description = expansion.getDescription();
            String fullName = description.getFullName();
            logger.info("Disabling expansion '" + fullName + "'...");

            List<Listener> listenerList = expansion.getListeners();
            listenerList.forEach(HandlerList::unregisterAll);
            listenerList.clear();

            expansion.setState(State.DISABLED);
            expansion.onDisable();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error occurred while disabling an expansion:", ex);
        }
    }

    private @NotNull List<Expansion> sortExpansions(@NotNull List<Expansion> original) {
        ExpansionComparator comparator = new ExpansionComparator();
        original.sort(comparator);
        return original;
    }

    private @NotNull YamlConfiguration getExpansionDescription(@NotNull JarFile jarFile)
            throws IllegalStateException, IOException, InvalidConfigurationException {
        JarEntry entry = jarFile.getJarEntry("expansion.yml");
        if (entry == null) {
            String errorMessage = ("Expansion file '" + jarFile.getName() + "' does not contain an " +
                    "expansion.yml file.");
            throw new IllegalStateException(errorMessage);
        }

        InputStream inputStream = jarFile.getInputStream(entry);
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(reader);

        YamlConfiguration description = new YamlConfiguration();
        description.load(buffer);
        return description;
    }
}
