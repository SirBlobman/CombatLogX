package com.SirBlobman.combatlogx.config;

import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigLang extends Config {
    private static final File FILE = new File(FOLDER, "language.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static YamlConfiguration load() {
        if (!FILE.exists()) copyFromJar("language.yml", FOLDER);
        config = load(FILE);
        return config;
    }

    private static String getMessage(String path) {
        if(!config.isSet(path)) return path;
        if(config.isString(path)) return config.getString(path);
        if(config.isList(path)) {
            List<String> messageList = config.getStringList(path);
            String[] messageArray = messageList.toArray(new String[0]);
            return String.join("\n", messageArray);
        }
        return path;
    }

    public static String get(String path) {
        String message = getMessage(path);
        return Util.color(message);
    }

    public static String getWithPrefix(String path) {
        String message = get(path);
        String prefix = get("messages.plugin prefix");

        String combined = message.isEmpty() ? "" : (prefix.isEmpty() ? message : (prefix + " " + message));
        return Util.color(combined);
    }
}