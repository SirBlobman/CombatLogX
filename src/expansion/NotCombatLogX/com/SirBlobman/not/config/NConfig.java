package com.SirBlobman.not.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.not.NotCombatLogX;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class NConfig extends Config {
    private static final File FOLDER = NotCombatLogX.FOLDER;
    private static final File FILE = new File(FOLDER, "not.yml");
    private static YamlConfiguration config = new YamlConfiguration();
    
    public static void save() {save(config, FILE);}
    public static YamlConfiguration load() {
        config = load(FILE);
        defaults();
        return config;
    }
    
    public static boolean TRIGGER_DROWNING = true;
    public static boolean TRIGGER_EXPLOSION = true;
    public static boolean TRIGGER_LAVA = true;
    public static boolean TRIGGER_FALL = true;
    public static boolean TRIGGER_PROJECTILE = true;
    public static boolean TRIGGER_ALL_DAMAGE = true;
    
    public static String MESSAGE_DROWNING = "";
    public static String MESSAGE_EXPLOSION = "";
    public static String MESSAGE_LAVA = "";
    public static String MESSAGE_FALL = "";
    public static String MESSAGE_PROJECTILE = "";
    public static String MESSAGE_UNKNOWN = "";
    
    private static void defaults() {
        TRIGGER_DROWNING = get(config, "triggers.drowning", true);
        TRIGGER_EXPLOSION = get(config, "triggers.block explosion", true);
        TRIGGER_LAVA = get(config, "triggers.lava", true);
        TRIGGER_FALL = get(config, "triggers.fall", true);
        TRIGGER_PROJECTILE = get(config, "triggers.projectile", true);
        TRIGGER_ALL_DAMAGE = get(config, "triggers.all damage", true);
        
        MESSAGE_DROWNING = get(config, "messages.drowning", "You are drowning! Do not log out.");
        MESSAGE_EXPLOSION = get(config, "messages.block explosion", "You suffered explosion damage! Do not log out.");
        MESSAGE_LAVA = get(config, "messages.lava", "You are melting in lava! Do not log out.");
        MESSAGE_FALL = get(config, "messages.fall", "You fell down! Do not log out.");
        MESSAGE_PROJECTILE = get(config, "messages.projectile", "You were shot by a projectile! Do not log out.");
        MESSAGE_UNKNOWN = get(config, "messages.unknown", "You took damage! Do not log out.");
        save();
    }
}