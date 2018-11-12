package com.SirBlobman.combatlogx.config;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.Util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    protected static final File FOLDER = CombatLogX.FOLDER;

    protected static void copyFromJar(String fileName, File folder) {
        try {
            InputStream is = Util.PLUGIN.getResource("resources/" + fileName);
            File newFile = new File(folder, fileName);
            if (!folder.exists()) folder.mkdirs();
            if (!newFile.exists()) {
                if (is != null) {
                    String pathString = newFile.getPath();
                    Path path = Paths.get(pathString);
                    Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    String error = "The file '" + fileName + "' does not exist in the jar.";
                    Bukkit.getConsoleSender().sendMessage(error);
                }
            } else {
                String error = "The file '" + fileName + "' already exists in '" + folder + "'.";
                Bukkit.getConsoleSender().sendMessage(error);
            }
        } catch (Throwable ex) {
            String error = "Failed to copy file '" + fileName + "' to '" + folder + "' from JAR:";
            Bukkit.getConsoleSender().sendMessage(error);
            ex.printStackTrace();
        }
    }

    protected static YamlConfiguration load(String name) {
        File file = new File(FOLDER, name + ".yml");
        return load(file);
    }

    protected static YamlConfiguration load(File file) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            if (!file.exists()) save(config, file);
            config.load(file);
            return config;
        } catch (Throwable ex) {
            String error = "Failed to load config '" + file + "':";
            Bukkit.getConsoleSender().sendMessage(error);
            ex.printStackTrace();
            return null;
        }
    }

    protected static void save(YamlConfiguration config, File file) {
        try {
            if (!file.exists()) {
                FOLDER.mkdirs();
                file.createNewFile();
            }
            config.save(file);
        } catch (Throwable ex) {
            String error = "Failed to save config '" + file + "':";
            Bukkit.getConsoleSender().sendMessage(error);
            ex.printStackTrace();
        }
    }

    /**
     * Gets a config value of the same type as defaultValue {@link T}<br/>
     *
     * @param config       YamlConfiguration to use
     * @param path         String path to the option
     * @param defaultValue If the value does not exist, it will become this
     * @return The value at {@code path}, if it is null or not the same type, {@code defaultValue} will be returned
     */
    @SuppressWarnings("unchecked")
    protected static <T> T get(YamlConfiguration config, String path, T defaultValue) {
        if (config.isSet(path)) {
            Object o = config.get(path);
            Class<?> clazz = defaultValue.getClass();
            if (clazz.isInstance(o)) {
                return (T) o;
            } else {
                config.set(path, defaultValue);
                return defaultValue;
            }
        } else {
            config.set(path, defaultValue);
            return defaultValue;
        }
    }
}