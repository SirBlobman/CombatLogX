package com.SirBlobman.combatlogx.api.expansion;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.api.ICombatLogX;

public final class ExpansionManager {
    private static final Map<String, Expansion> nameToExpansionMap = Util.newMap();

    public static void loadExpansions(ICombatLogX plugin) {
        try {
            File dataFolder = plugin.getDataFolder();
            File expansionsFolder = new File(dataFolder, "expansions");
            if(!expansionsFolder.exists()) {
                Logger logger = plugin.getLogger();
                logger.info("Creating expansions folder...");
                expansionsFolder.mkdirs();
            }

            FilenameFilter filenameFilter = (folder, fileName) -> fileName.endsWith(".jar");
            File[] fileArray = expansionsFolder.listFiles(filenameFilter);
            if(fileArray == null || fileArray.length == 0) return;

            for(File file : fileArray) checkFileForExpansion(plugin, file);

            List<Expansion> expansionList = getExpansions();
            int expansionCount = expansionList.size();

            String amount = Integer.toString(expansionCount);
            String s = (expansionCount == 1 ? "" : "s");

            String message = plugin.getLanguageMessage("expansion-logging.loaded").replace("{amount}", amount).replace("{s}", s);
            Logger logger = plugin.getLogger();
            logger.info(message);
        } catch(IOException | ReflectiveOperationException ex) {
            Logger logger = plugin.getLogger();
            logger.log(Level.SEVERE, "An error occurred while loading expansions.", ex);
        }
    }

    public static void enableExpansions(ICombatLogX plugin) {
        Logger logger = plugin.getLogger();
        List<Expansion> expansionList = getExpansions();
        for(Expansion expansion : expansionList) {
            String expName = expansion.getName();
            String expUnlocalizedName = expansion.getUnlocalizedName();
            String expVersion = expansion.getVersion();

            String message = plugin.getLanguageMessage("expansion-logging.enabling").replace("{name}", expName).replace("{unlocalized_name}", expUnlocalizedName).replace("{version}", expVersion);
            logger.info(message);

            try {
                expansion.onEnable();
                logger.info(" ");
            } catch(Exception ex) {
                logger.log(Level.SEVERE, "An error occurred while enabling an expansion.", ex);
                logger.info(" ");
            }
        }

        int expansionCount = expansionList.size();
        String amount = Integer.toString(expansionCount);
        String s = (expansionCount == 1 ? "" : "s");

        String message = plugin.getLanguageMessage("expansion-logging.loaded").replace("{amount}", amount).replace("{s}", s);
        logger.info(message);
    }

    public static void disableExpansions() {
        List<Expansion> expansionList = getExpansions();
        for(Expansion expansion : expansionList) expansion.onDisable();

        nameToExpansionMap.clear();
    }

    public static List<Expansion> getExpansions() {
        return Util.newList(nameToExpansionMap.values());
    }

    public static boolean isEnabled(String unlocalizedExpansionName) {
        return nameToExpansionMap.containsKey(unlocalizedExpansionName);
    }

    public static Expansion gtExpansion(String unlocalizedExpansionName) {
        return nameToExpansionMap.getOrDefault(unlocalizedExpansionName, null);
    }

    public static void reloadConfigs() {
        List<Expansion> expansionList = getExpansions();
        for(Expansion expansion : expansionList) expansion.reloadConfig();
    }

    private static void checkFileForExpansion(ICombatLogX plugin, File file) throws IOException, ReflectiveOperationException {
        if(file.isDirectory()) return;
        if(isNotJar(file)) return;

        JarFile jarFile = loadJAR(plugin, file);
        if(jarFile == null) return;

        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while(jarEntryEnumeration != null && jarEntryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if(jarEntry.isDirectory()) continue;

            String entryName = getClassName(jarEntry);
            if(entryName == null) continue;

            Class<?> entryClass;
            try {
                entryClass = Class.forName(entryName);
            } catch(ReflectiveOperationException ex) {
                continue;
            }

            if(loadExpansion(plugin, entryClass)) break;
        }
    }

    private static boolean isNotJar(File file) {
        if(file == null) return true;

        String fileName = file.getName();
        return !fileName.endsWith(".jar");
    }

    private static synchronized JarFile loadJAR(ICombatLogX plugin, File file) throws IOException, ReflectiveOperationException, SecurityException {
        JarFile jarFile = new JarFile(file);
        ClassLoader classLoader = plugin.getPluginClassLoader();
        if(!(classLoader instanceof URLClassLoader)) return null;

        URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
        URI fileURI = file.toURI();
        URL fileURL = fileURI.toURL();
        for(URL url : urlClassLoader.getURLs()) {
            if(url.equals(fileURL)) return jarFile;
        }

        Class<URLClassLoader> class_URLClassLoader = URLClassLoader.class;
        Method method_addURL = class_URLClassLoader.getDeclaredMethod("addURL", URL.class);
        method_addURL.setAccessible(true);

        method_addURL.invoke(urlClassLoader, fileURL);
        return jarFile;
    }

    private static boolean isNotClass(JarEntry entry) {
        if(entry == null) return true;

        String entryName = entry.getName();
        return !entryName.endsWith(".class");
    }

    private static String getClassName(JarEntry entry) {
        if(isNotClass(entry)) return null;

        String entryName = entry.getName();
        return entryName.replace(".class", "").replace("/", ".").replace(File.separator, ".");
    }

    private static boolean loadExpansion(ICombatLogX plugin, Class<?> expansionClass) {
        Logger logger = plugin.getLogger();
        try {
            Class<?> class_Expansion = Expansion.class;
            if(!class_Expansion.isAssignableFrom(expansionClass)) return false;

            Constructor<?> constructor = expansionClass.getDeclaredConstructor(ICombatLogX.class);
            Expansion expansion = (Expansion) constructor.newInstance(plugin);

            String expName = expansion.getName();
            String expUnlocalizedName = expansion.getUnlocalizedName();
            String expVersion = expansion.getVersion();

            String message = plugin.getLanguageMessage("expansion-logging.loading").replace("{name}", expName).replace("{unlocalized_name}", expUnlocalizedName).replace("{version}", expVersion);
            logger.info(message);

            nameToExpansionMap.put(expUnlocalizedName, expansion);
            expansion.onLoad();
            return true;
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Failed to load an expansion.", ex);
            return false;
        }
    }

    public static void unloadExpansion(Expansion expansion) {
        String expUnlocalizedName = expansion.getUnlocalizedName();
        nameToExpansionMap.remove(expUnlocalizedName);

        expansion.onDisable();
    }
}