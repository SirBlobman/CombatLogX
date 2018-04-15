package com.SirBlobman.npc.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.npc.CompatCitizens;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ConfigData extends Config {
    private static final File FOLDER = CompatCitizens.FOLDER;
    private static final File FILE = new File(FOLDER, "data.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static YamlConfiguration load() {
        try {
            config = Config.load(FILE);
        } catch(Throwable ex) {
            String error = "Failed to load '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
        return config;
    }
    
    public static void save() {
        try {
            Config.save(config, FILE);
        } catch(Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }
    
    public static <T> T get(Player p, String path, T defaultValue) {
        UUID uuid = p.getUniqueId();
        String id = uuid.toString();
        String npath = id + "." + path;
        T t = get(config, npath, defaultValue);
        save();
        return t;
    }
    
    public static void force(OfflinePlayer op, String path, Object value) {
        UUID uuid = op.getUniqueId();
        String id = uuid.toString();
        String npath = id + "." + path;
        config.set(npath, value);
        save();
    }
    
    public static void remove(OfflinePlayer op) {
        UUID uuid = op.getUniqueId();
        String id = uuid.toString();
        config.set(id, null);
        save();
    }
    
    public static boolean exists(OfflinePlayer op) {
        UUID uuid = op.getUniqueId();
        String id = uuid.toString();
        load();
        return config.contains(id);
    }
}