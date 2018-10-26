package com.SirBlobman.combatlogx.config;

import com.SirBlobman.combatlogx.CombatLogX;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    protected static final File FOLDER = CombatLogX.FOLDER;
    
    protected static YamlConfiguration load(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) save(config, file);
        config.load(file);
        return config;
    }
    
    protected static void save(YamlConfiguration config, File file) throws IOException, InvalidConfigurationException {
        if (!file.exists()) {
            File folder = file.getParentFile();
            folder.mkdirs();
            file.createNewFile();
        }
        config.save(file);
    }
    
    @SuppressWarnings("unchecked")
    protected static <T> T get(YamlConfiguration config, String path, T defaultValue) {
        Object o = config.get(path);
        Class<?> clazz = defaultValue.getClass();
        if (o != null && clazz.isInstance(o)) {
            T t = (T) clazz.cast(o);
            return t;
        } else {
            config.set(path, defaultValue);
            return defaultValue;
        }
    }
}