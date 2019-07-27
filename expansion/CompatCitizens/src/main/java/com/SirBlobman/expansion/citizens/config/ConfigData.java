package com.SirBlobman.expansion.citizens.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.CompatCitizens;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class ConfigData extends Config {
    private static final File FOLDER = CompatCitizens.FOLDER;
    private static final File FUSERS = new File(FOLDER, "users");

    private static void save(OfflinePlayer op, YamlConfiguration config) {
        try {
            UUID uuid = op.getUniqueId();
            String id = uuid.toString();
            String fileName = id + ".yml";
            if (!FUSERS.exists()) FUSERS.mkdirs();

            File file = new File(FUSERS, fileName);
            if (!file.exists()) file.createNewFile();

            save(config, file);
        } catch (Throwable ex) {
            String error = "Failed to save CompatCitizens data for '" + op.getName() + "'.";
            Util.print(error);
            ex.printStackTrace();
        }
    }

    private static YamlConfiguration load(OfflinePlayer op) {
        YamlConfiguration config = new YamlConfiguration();

        UUID uuid = op.getUniqueId();
        String id = uuid.toString();
        String fileName = id + ".yml";
        File file = new File(FUSERS, fileName);

        if (!file.exists()) save(op, config);

        config = load(file);
        return config;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(OfflinePlayer op, String path, T defaultValue) {
        YamlConfiguration config = load(op);
        if (config.isSet(path)) {
            Object val = config.get(path);
            Class<?> clazz = defaultValue.getClass();
            if (clazz.isInstance(val)) {
                return (T) val;
            } else {
                String error = "The config value for '" + path + "' is not set to the type '" + clazz.getSimpleName() + "'";
                Util.print(error);
                return defaultValue;
            }
        } else {
            config.set(path, defaultValue);
            save(op, config);
            return defaultValue;
        }
    }

    public static void force(OfflinePlayer op, String path, Object value) {
        YamlConfiguration config = load(op);
        config.set(path, value);
        save(op, config);
    }
}