package com.SirBlobman.combatlogx.config;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigOptions extends Config {
    public static boolean OPTION_DEBUG;
    public static boolean OPTION_CHECK_FOR_UPDATES;
    public static List<String> OPTION_DISABLED_WORLDS;
    public static boolean OPTION_BROADCAST_ENABLE_MESSAGE;
    public static boolean OPTION_BROADCAST_DISABLE_MESSAGE;
    public static int OPTION_TIMER;
    public static boolean OPTION_LINK_PROJECTILES;
    public static boolean OPTION_LINK_PETS;
    public static boolean PUNISH_ON_KICK;
    public static boolean PUNISH_ON_QUIT;
    public static boolean PUNISH_ON_EXPIRE;
    public static boolean PUNISH_KILL;
    public static boolean PUNISH_SUDO;
    public static List<String> PUNISH_SUDO_COMMANDS;
    public static boolean COMBAT_SELF;
    public static boolean COMBAT_MOBS;
    public static List<String> COMBAT_MOBS_BLACKLIST;
    public static boolean COMBAT_SUDO;
    public static List<String> COMBAT_SUDO_COMMANDS;
    public static boolean COMBAT_UNTAG_ON_ENEMY_DEATH;
    public static boolean COMBAT_UNTAG_ON_SELF_DEATH;
    public static boolean COMBAT_BYPASS_ALLOW;
    public static String COMBAT_BYPASS_PERMISSION;

    public static YamlConfiguration load() {
        throw new UnsupportedOperationException();
    }
}