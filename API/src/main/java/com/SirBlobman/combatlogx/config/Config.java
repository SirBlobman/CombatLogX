package com.SirBlobman.combatlogx.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    protected static void copyFromJar(String fileName, File folder) {
        throw new UnsupportedOperationException();
    }

    protected static YamlConfiguration load(String name) {
        throw new UnsupportedOperationException();
    }

    protected static YamlConfiguration load(File file) {
        throw new UnsupportedOperationException();
    }

    protected static void save(YamlConfiguration config, File file) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a config value of the same type as defaultValue {@link T}<br/>
     *
     * @param config       YamlConfiguration to use
     * @param path         String path to the option
     * @param defaultValue If the value does not exist, it will become this
     * @return The value at {@code path}, if it is null or not the same type, {@code defaultValue} will be returned
     */
    protected static <T> T get(YamlConfiguration config, String path, T defaultValue) {
        throw new UnsupportedOperationException();
    }
}