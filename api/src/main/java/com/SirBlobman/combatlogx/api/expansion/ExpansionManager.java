package com.SirBlobman.combatlogx.api.expansion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion.State;

public class ExpansionManager {
    private final ICombatLogX plugin;
    private final List<Expansion> expansionList;
    private final Map<Expansion, List<Listener>> expansionListenerMap;
    private final Map<Expansion, ExpansionClassLoader> classLoaderMap;
    private final Map<String, Class<?>> classNameMap;
    public ExpansionManager(ICombatLogX plugin) {
        this.plugin = plugin;
        this.expansionList = new ArrayList<>();
        this.expansionListenerMap = new HashMap<>();
        this.classLoaderMap = new HashMap<>();
        this.classNameMap = new HashMap<>();
    }
    
    public ICombatLogX getPlugin() {
        return this.plugin;
    }
    
    public void loadExpansions() {
        Logger logger = this.plugin.getLogger();
        logger.info("Loading expansions...");
        
        File pluginFolder = this.plugin.getDataFolder();
        if(!pluginFolder.exists()) {
            boolean create = pluginFolder.mkdirs();
            if(!create) {
                logger.warning("The plugin folder could not be created.");
                return;
            }
        }
        
        File expansionsFolder = new File(pluginFolder, "expansions");
        if(!expansionsFolder.exists()) {
            boolean create = expansionsFolder.mkdirs();
            if(!create) {
                logger.warning("The expansion folder could not be created.");
                return;
            }
        }
    
        FilenameFilter filter = (folder, fileName) -> fileName.endsWith(".jar");
        File[] fileArray = expansionsFolder.listFiles(filter);
        if(fileArray == null || fileArray.length == 0) {
            logger.info("There are no expansions to load.");
            return;
        }
        
        for(File file : fileArray) {
            if(file.isDirectory()) continue;
            loadExpansion(file);
        }
        
        List<Expansion> loadedExpansionList = getLoadedExpansions();
        if(!loadedExpansionList.isEmpty()) sortExpansions();
        
        int loadedExpansionListSize = loadedExpansionList.size();
        logger.info("Successfully loaded " + loadedExpansionListSize + " expansions.");
    }
    
    public void enableExpansions() {
        Logger logger = this.plugin.getLogger();
        List<Expansion> loadedExpansionList = getLoadedExpansions();
        if(loadedExpansionList.isEmpty()) {
            logger.info("There are no expansions to enable.");
            return;
        }
        loadedExpansionList.forEach(this::enableExpansion);
        
        List<Expansion> enabledExpansionList = getEnabledExpansions();
        int enabledExpansionListSize = enabledExpansionList.size();
        logger.info("Successfully enabled " + enabledExpansionListSize + " expansions.");
    }
    
    public void disableExpansions() {
        List<Expansion> enabledExpansionList = getEnabledExpansions();
        if(!enabledExpansionList.isEmpty()) {
            Logger logger = this.plugin.getLogger();
            logger.info("Disabling expansions...");
            enabledExpansionList.forEach(this::disableExpansion);
            logger.info("Successfully disabled all expansions.");
        }
        
        this.expansionList.clear();
        this.classLoaderMap.clear();
        this.classNameMap.clear();
    }
    
    public void reloadExpansionConfigs() {
        List<Expansion> enabledExpansionList = getEnabledExpansions();
        enabledExpansionList.forEach(Expansion::reloadConfig);
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Expansion> Optional<E> getExpansionByName(String name) {
        if(name == null) return Optional.empty();
        return this.expansionList.stream()
                .filter(expansion -> expansion.getDescription().getName().equalsIgnoreCase(name))
                .map(expansion -> (E) expansion)
                .findFirst();
    }
    
    public List<Expansion> getLoadedExpansions() {
        return this.expansionList.stream()
                .filter(expansion -> expansion.getState() == State.LOADED)
                .collect(Collectors.toList());
    }
    
    
    public List<Expansion> getEnabledExpansions() {
        return this.expansionList.stream()
                .filter(expansion -> expansion.getState() == State.ENABLED)
                .collect(Collectors.toList());
    }
    
    public List<Expansion> getAllExpansions() {
        return new ArrayList<>(this.expansionList);
    }
    
    public ExpansionClassLoader getClassLoader(Expansion expansion) {
        return this.classLoaderMap.get(expansion);
    }
    
    public Class<?> getClassByName(String name) {
        try {
            Class<?> defaultValue = this.classLoaderMap.values().stream().map(loader -> loader.findClass(name, false)).filter(Objects::nonNull).findFirst().orElse(null);
            return this.classNameMap.getOrDefault(name, defaultValue);
        } catch(Exception ex) {
            return null;
        }
    }
    
    public void setClass(String name, Class<?> clazz) {
        this.classNameMap.putIfAbsent(name, clazz);
    }
    
    public void registerListener(Expansion expansion, Listener listener) {
        PluginManager manager = Bukkit.getPluginManager();
        JavaPlugin plugin = this.plugin.getPlugin();
        
        manager.registerEvents(listener, plugin);
        this.expansionListenerMap.computeIfAbsent(expansion, e -> new ArrayList<>()).add(listener);
    }
    
    private void loadExpansion(File file) {
        Expansion expansion;
        ExpansionClassLoader classLoader;
        
        try (JarFile jarFile = new JarFile(file)) {
            YamlConfiguration description = getExpansionDescription(jarFile);
            Class<? extends ExpansionManager> managerClass = getClass();
            ClassLoader managerClassLoader = managerClass.getClassLoader();
            
            classLoader = new ExpansionClassLoader(this, description, file, managerClassLoader);
            expansion = classLoader.getExpansion();
        } catch(Exception ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.SEVERE, "An error occurred while trying to load an expansion", ex);
            return;
        }

        File pluginFolder = this.plugin.getDataFolder();
        File expansionsFolder = new File(pluginFolder, "expansions");
        String expansionName = expansion.getDescription().getName();
        File dataFolder = new File(expansionsFolder, expansionName);
        expansion.setDataFolder(dataFolder);
        expansion.setFile(file);
        
        this.expansionList.remove(expansion);
        this.expansionList.add(expansion);
        this.classLoaderMap.put(expansion, classLoader);
        
        try {
            expansion.setState(State.LOADED);
            expansion.onLoad();
        } catch(Throwable ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.SEVERE, "An error occurred while loading an expansion", ex);
        }
    }
    
    public void enableExpansion(Expansion expansion) {
        if(expansion.getState() == State.ENABLED) return;
        
        try {
            Logger logger = this.plugin.getLogger();
            String expansionName = expansion.getDescription().getFullName();
            logger.info("Enabling expansion '" + expansionName + "'...");
    
            expansion.setState(State.ENABLED);
            expansion.onEnable();
        } catch(Throwable ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.SEVERE, "An error occurred while enabling an expansion.", ex);
        }
    }
    
    public void disableExpansion(Expansion expansion) {
        if(expansion.getState() != State.ENABLED) return;
        
        try {
            Logger logger = this.plugin.getLogger();
            String expansionName = expansion.getDescription().getFullName();
            logger.info("Disabling expansion '" + expansionName + "'...");
        
            expansion.onDisable();
            expansion.setState(State.DISABLED);
    
            List<Listener> listenerList = this.expansionListenerMap.get(expansion);
            if(listenerList == null || listenerList.isEmpty()) return;
            listenerList.forEach(HandlerList::unregisterAll);
        } catch(Throwable ex) {
            Logger logger = this.plugin.getLogger();
            logger.log(Level.SEVERE, "An error occurred while enabling an expansion.", ex);
        }
    }
    
    private void sortExpansions() {
        Comparator<Expansion> expansionComparator = (expansion1, expansion2) -> {
            ExpansionDescription expansionDescription1 = expansion1.getDescription();
            ExpansionDescription expansionDescription2 = expansion2.getDescription();
            
            String expansionName1 = expansionDescription1.getName();
            String expansionName2 = expansionDescription2.getName();
            return expansionName1.compareTo(expansionName2);
        };
        this.expansionList.sort(expansionComparator);
    }
    
    private YamlConfiguration getExpansionDescription(JarFile jarFile) throws IllegalStateException, IOException, InvalidConfigurationException {
        JarEntry entry = jarFile.getJarEntry("expansion.yml");
        if(entry == null) {
            String errorMessage = ("Expansion file '" + jarFile.getName() + "' does not contain an expansion.yml file.");
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
