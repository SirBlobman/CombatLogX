package com.SirBlobman.combatlogx.config;

import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigOptions extends Config {
    private static final File FILE = new File(FOLDER, "combat.yml");
    private static YamlConfiguration CONFIG = new YamlConfiguration();
    
    public static YamlConfiguration load() {
        try {
            CONFIG = Config.load(FILE);
            defaults();
        } catch(Throwable ex) {
            String error = "Failed to load '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
        return CONFIG;
    }
    
    public static void save() {
        try {
            Config.save(CONFIG, FILE);
        } catch(Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }
    

    public static int OPTION_TIMER = 30;
    public static boolean OPTION_CHECK_UPDATES = true;
    public static boolean OPTION_LOG_TO_FILE = true;
    public static boolean OPTION_LOG_TO_CONSOLE = true;
    public static boolean OPTION_BROADCAST_STARTUP = true;
    public static boolean OPTION_ACTION_BAR = true;
    public static boolean OPTION_BOSS_BAR = true;
    public static String OPTION_BOSS_BAR_COLOR = "YELLOW";
    public static boolean OPTION_SCORE_BOARD = true;
    public static boolean OPTION_SELF_COMBAT = true;
    public static boolean OPTION_MOBS_COMBAT = true;
    public static boolean OPTION_BYPASS_ENABLE = false;
    public static boolean OPTION_REMOVE_COMBAT_ON_ENEMY_DEATH = true;
    public static boolean OPTION_COMBAT_SUDO_ENABLE = true;
    public static String OPTION_BYPASS_PERMISSION = "combatlogx.bypass";
    public static List<String> OPTION_MOBS_BLACKLIST = Util.newList("PIG", "COW");
    public static List<String> OPTION_DISABLED_WORLDS = Util.newList("WoRlD", "Lobby", "Creative");
    public static List<String> OPTION_COMBAT_SUDO_COMMANDS = Util.newList("say I am now in combat");

    public static boolean CHEAT_PREVENT_BLOCKED_COMMANDS_MODE = false;
    public static boolean CHEAT_PREVENT_DISABLE_FLIGHT = true;
    public static boolean CHEAT_PREVENT_ENABLE_FLIGHT = true;
    public static boolean CHEAT_PREVENT_NO_ENTRY = true;
    public static boolean CHEAT_PREVENT_CHANGE_GAMEMODE = true;
    public static boolean CHEAT_PREVENT_OPEN_INVENTORIES = true;
    public static boolean CHEAT_PREVENT_TELEPORT = true;
    public static boolean CHEAT_PREVENT_TELEPORT_ALLOW_ENDERPEARLS = true;
    public static String CHEAT_PREVENT_CHANGE_GAMEMODE_MODE = "SURVIVAL";
    public static String CHEAT_PREVENT_NO_ENTRY_MODE = "CANCEL";
    public static int CHEAT_PREVENT_NO_ENTRY_STRENGTH = 0;
    public static List<String> CHEAT_PREVENT_BLOCKED_COMMANDS = Util.newList("fly", "tpa", "tpahere", "spawn", "home");
    public static List<String> CHEAT_PREVENT_BLOCKED_POTIONS = Util.newList("INVISIBILITY", "INCREASE_DAMAGE");

    public static boolean PUNISH_ON_KICK = false;
    public static boolean PUNISH_ON_QUIT = true;
    public static boolean PUNISH_ON_QUIT_MESSAGE = true;
    public static boolean PUNISH_SUDO_LOGGERS = false;
    public static boolean PUNISH_KILL_PLAYER = true;
    public static boolean PUNISH_CONSOLE = true;

    public static List<String> PUNISH_COMMANDS_CONSOLE = Util.newList("eco take {player} 100", "mail send {player} You lost $100 due to logging out during combat!");
    public static List<String> PUNISH_COMMANDS_LOGGERS = Util.newList("say I logged out of combat!");
    
    private static void defaults() {
        OPTION_BROADCAST_STARTUP = get("options.broadcast enable message", true);
        OPTION_LOG_TO_FILE = get("options.log to file", true);
        OPTION_LOG_TO_CONSOLE = get("options.log to console", false);
        OPTION_CHECK_UPDATES = get("options.check for updates", true);
        OPTION_DISABLED_WORLDS = get("options.disabled worlds", Util.newList("WoRlD", "Lobby", "Creative"));
        OPTION_TIMER = get("options.combat timer", 30);
        OPTION_ACTION_BAR = get("options.action bar", true);
        OPTION_BOSS_BAR = get("options.boss bar.enable", true);
        OPTION_BOSS_BAR_COLOR = get("options.boss bar.color", "YELLOW");
        OPTION_SCORE_BOARD = get("options.score board", true);
        OPTION_SELF_COMBAT = get("options.self combat", true);
        OPTION_MOBS_COMBAT = get("options.mobs.combat", true);
        OPTION_MOBS_BLACKLIST = get("options.mobs.blacklist", Util.newList("PIG", "COW"));
        OPTION_REMOVE_COMBAT_ON_ENEMY_DEATH = get("options.remove combat on enemy death", true);
        OPTION_BYPASS_ENABLE = get("options.bypass.enable", false);
        OPTION_BYPASS_PERMISSION = get("options.bypass.permission", "combatlogx.bypass");
        OPTION_COMBAT_SUDO_ENABLE = get("options.combat sudo.enable", true);
        OPTION_COMBAT_SUDO_COMMANDS = get("options.combat sudo.commands", Util.newList("say I am now in combat"));

        CHEAT_PREVENT_OPEN_INVENTORIES = get("cheat prevention.prevent opening inventories", true);
        CHEAT_PREVENT_TELEPORT = get("cheat prevention.teleportation.prevent", true);
        CHEAT_PREVENT_TELEPORT_ALLOW_ENDERPEARLS = get("cheat prevention.teleportation.allow ender pearls", false);
        CHEAT_PREVENT_NO_ENTRY = get("cheat prevention.safezone.no entry", true);
        CHEAT_PREVENT_NO_ENTRY_MODE = get("cheat prevention.safezone.mode", "KNOCKBACK").toUpperCase();
        CHEAT_PREVENT_NO_ENTRY_STRENGTH = get("cheat prevention.safezone.knockback strength", 5);
        CHEAT_PREVENT_DISABLE_FLIGHT = get("cheat prevention.flight.disable", true);
        CHEAT_PREVENT_ENABLE_FLIGHT = get("cheat prevention.flight.re-enable after combat", false);
        CHEAT_PREVENT_CHANGE_GAMEMODE = get("cheat prevention.change gamemode.enabled", true);
        CHEAT_PREVENT_CHANGE_GAMEMODE_MODE = get("cheat prevention.change gamemode.mode", "SURVIVAL");
        CHEAT_PREVENT_BLOCKED_COMMANDS_MODE = get("cheat prevention.blocked commands.whitelist mode", false);
        CHEAT_PREVENT_BLOCKED_COMMANDS = get("cheat prevention.blocked commands.commands", Util.newList("fly", "tpa", "tpahere", "spawn", "home"));
        CHEAT_PREVENT_BLOCKED_POTIONS = get("cheat prevention.blocked potions", Util.newList("INVISIBILITY", "INCREASE_DAMAGE"));

        PUNISH_KILL_PLAYER = get("punishment.kill loggers", true);
        PUNISH_ON_KICK = get("punishment.kick", false);
        PUNISH_ON_QUIT = get("punishment.quit.enable", true);
        PUNISH_ON_QUIT_MESSAGE = get("punishment.quit.message", true);
        PUNISH_CONSOLE = get("punishment.console.enable", true);
        PUNISH_COMMANDS_CONSOLE = get("punishment.console.commands",  Util.newList("eco take {player} 100", "mail send {player} You lost $100 due to logging out during combat!"));
        PUNISH_SUDO_LOGGERS = get("punishment.sudo loggers.enable", false);
        PUNISH_COMMANDS_LOGGERS = get("punishment.sudo loggers.commands", Util.newList("say I logged out of combat!"));
        save();
    }
    
    private static <T> T get(String path, T defaultValue) {return Config.get(CONFIG, path, defaultValue);}
}