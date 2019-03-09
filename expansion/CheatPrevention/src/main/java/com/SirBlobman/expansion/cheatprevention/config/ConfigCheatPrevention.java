package com.SirBlobman.expansion.cheatprevention.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.CheatPrevention;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigCheatPrevention extends Config {
    private static File FOLDER = CheatPrevention.FOLDER;
    private static File FILE = new File(FOLDER, "cheat prevention.yml");
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        if (FOLDER == null) FOLDER = CheatPrevention.FOLDER;
        if (FILE == null) FILE = new File(FOLDER, "cheat prevention.yml");

        if (!FILE.exists()) copyFromJar("cheat prevention.yml", FOLDER);
        config = load(FILE);
        defaults();
    }
    
    public static boolean TELEPORTATION_ALLOW_DURING_COMBAT;
    public static boolean TELEPORTATION_ENDER_PEARLS_RESTART_TIMER;
    public static List<String> TELEPORTATION_ALLOWED_CAUSES;
    
    public static boolean FLIGHT_ALLOW_DURING_COMBAT;
    public static boolean FLIGHT_ALLOW_ELYTRAS;
    public static String FLIGHT_ENABLE_PERMISSION;
    
    public static boolean GAMEMODE_CHANGE_WHEN_TAGGED;
    public static String GAMEMODE_GAMEMODE;
    
    public static boolean BLOCKED_COMMANDS_IS_WHITELIST;
    public static List<String> BLOCKED_COMMANDS_LIST;
    
    public static boolean INVENTORY_CLOSE_ON_COMBAT;
    public static boolean INVENTORY_PREVENT_OPENING;
    
    public static boolean CHAT_ALLOW_DURING_COMBAT;
    
    public static List<String> BLOCKED_POTIONS;

    private static void defaults() {
        TELEPORTATION_ALLOW_DURING_COMBAT = get(config, "teleportation.allow during combat", false);
        TELEPORTATION_ENDER_PEARLS_RESTART_TIMER = get(config, "teleportation.ender pearls restart timer", false);
        TELEPORTATION_ALLOWED_CAUSES = get(config, "teleportation.allowed causes", Util.newList("ENDER_PEARL", "PLUGIN"));

        FLIGHT_ALLOW_DURING_COMBAT = get(config, "flight.allow during combat", false);
        FLIGHT_ALLOW_ELYTRAS = get(config, "flight.allow elytras", false);
        FLIGHT_ENABLE_PERMISSION = get(config, "flight.enable permission", "combatlogx.flight.enable");

        GAMEMODE_CHANGE_WHEN_TAGGED = get(config, "gamemode.change", true);
        GAMEMODE_GAMEMODE = get(config, "gamemode.gamemode", "SURVIVAL").toUpperCase();

        BLOCKED_COMMANDS_IS_WHITELIST = get(config, "commands.whitelist", false);
        BLOCKED_COMMANDS_LIST = get(config, "commands.commands", Util.newList("tp", "fly", "gamemode"));

        INVENTORY_CLOSE_ON_COMBAT = get(config, "inventories.close on tag", true);
        INVENTORY_PREVENT_OPENING = get(config, "inventories.prevent opening", true);

        CHAT_ALLOW_DURING_COMBAT = get(config, "chat.allow during combat", true);
        
        BLOCKED_POTIONS = get(config, "potions.blocked potions", Util.newList("INVISIBILITY", "INCREASE_DAMAGE"));
    }
}