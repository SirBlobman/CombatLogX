package com.SirBlobman.combatlogx.config;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Config {
    public enum NoEntryMode {CANCEL, KNOCKBACK, KILL}
    protected static final File FOLDER = CombatLogX.FOLDER;
    private static final File FILEC = new File(FOLDER, "combat.yml");
    private static final File FILEL = new File(FOLDER, "language.yml");

    protected static YamlConfiguration load(File file) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            if(!file.exists()) save(config, file);
            config.load(file);
            return config;
        } catch(Throwable ex) {
            String error = Util.format("Failed to load file '%1s':\n%2s", file, ex.getMessage());
            Util.print(error);
            return null;
        }
    }

    public static YamlConfiguration loadC() {
        YamlConfiguration config = load(FILEC);
        defaultsC(config);
        return config;
    }

    public static YamlConfiguration loadL() {
        YamlConfiguration config = load(FILEL);
        defaultsL(config);
        return config;
    }

    public static void save(YamlConfiguration config, File file) {
        try {
            if(!file.exists()) {
                File folder = file.getParentFile();
                folder.mkdirs();
                file.createNewFile();
            } 
            config.save(file);
        } catch(Throwable ex) {
            String error = Util.format("Failed to save file '%1s':\n%2s", file, ex.getMessage());
            Util.print(error);
        }
    }

    /**
     * Gets a config value of the same type as defaultValue {@link T}<br/>
     * @param config YamlConfiguration to use
     * @param path String path to the option
     * @param defaultValue If the value does not exist, it will become this
     * @return The value at {@code path}, if it is null or not the same type, {@code defaultValue} will be returned
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(YamlConfiguration config, String path, T defaultValue) {
        Object o = config.get(path);
        Class<?> clazz = defaultValue.getClass();
        if(o != null && clazz.isInstance(o)) {
            T t = (T) clazz.cast(o);
            return t;
        } else {
            config.set(path, defaultValue);
            return defaultValue;
        }
    }
    //Start Config
    public static int OPTION_TIMER = 30;
    public static boolean OPTION_CHECK_UPDATES = true;
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

    private static void defaultsC(YamlConfiguration config) {
        OPTION_BROADCAST_STARTUP = get(config, "options.broadcast enable message", true);
        OPTION_CHECK_UPDATES = get(config, "options.check for updates", true);
        OPTION_DISABLED_WORLDS = get(config, "options.disabled worlds", Util.newList("WoRlD", "Lobby", "Creative"));
        OPTION_TIMER = get(config, "options.combat timer", 30);
        OPTION_ACTION_BAR = get(config, "options.action bar", true);
        OPTION_BOSS_BAR = get(config, "options.boss bar.enable", true);
        OPTION_BOSS_BAR_COLOR = get(config, "options.boss bar.color", "YELLOW");
        OPTION_SCORE_BOARD = get(config, "options.score board", true);
        OPTION_SELF_COMBAT = get(config, "options.self combat", true);
        OPTION_MOBS_COMBAT = get(config, "options.mobs.combat", true);
        OPTION_MOBS_BLACKLIST = get(config, "options.mobs.blacklist", Util.newList("PIG", "COW"));
        OPTION_REMOVE_COMBAT_ON_ENEMY_DEATH = get(config, "options.remove combat on enemy death", true);
        OPTION_BYPASS_ENABLE = get(config, "options.bypass.enable", false);
        OPTION_BYPASS_PERMISSION = get(config, "options.bypass.permission", "combatlogx.bypass");
        OPTION_COMBAT_SUDO_ENABLE = get(config, "options.combat sudo.enable", true);
        OPTION_COMBAT_SUDO_COMMANDS = get(config, "options.combat sudo.commands", Util.newList("say I am now in combat"));

        CHEAT_PREVENT_OPEN_INVENTORIES = get(config, "cheat prevention.prevent opening inventories", true);
        CHEAT_PREVENT_TELEPORT = get(config, "cheat prevention.prevent teleportation", true);
        CHEAT_PREVENT_NO_ENTRY = get(config, "cheat prevention.safezone.no entry", true);
        CHEAT_PREVENT_NO_ENTRY_MODE = get(config, "cheat prevention.safezone.mode", "KNOCKBACK").toUpperCase();
        CHEAT_PREVENT_NO_ENTRY_STRENGTH = get(config, "cheat prevention.safezone.knockback strength", 5);
        CHEAT_PREVENT_DISABLE_FLIGHT = get(config, "cheat prevention.flight.disable", true);
        CHEAT_PREVENT_ENABLE_FLIGHT = get(config, "cheat prevention.flight.re-enable after combat", false);
        CHEAT_PREVENT_CHANGE_GAMEMODE = get(config, "cheat prevention.change gamemode.enabled", true);
        CHEAT_PREVENT_CHANGE_GAMEMODE_MODE = get(config, "cheat prevention.change gamemode.mode", "SURVIVAL");
        CHEAT_PREVENT_BLOCKED_COMMANDS_MODE = get(config, "cheat prevention.blocked commands.whitelist mode", false);
        CHEAT_PREVENT_BLOCKED_COMMANDS = get(config, "cheat prevention.blocked commands.commands", Util.newList("fly", "tpa", "tpahere", "spawn", "home"));
        CHEAT_PREVENT_BLOCKED_POTIONS = get(config, "cheat prevention.blocked potions", Util.newList("INVISIBILITY", "INCREASE_DAMAGE"));

        PUNISH_KILL_PLAYER = get(config, "punishment.kill loggers", true);
        PUNISH_ON_KICK = get(config, "punishment.kick", false);
        PUNISH_ON_QUIT = get(config, "punishment.quit.enable", true);
        PUNISH_ON_QUIT_MESSAGE = get(config, "punishment.quit.message", true);
        PUNISH_CONSOLE = get(config, "punishment.console.enable", true);
        PUNISH_COMMANDS_CONSOLE = get(config, "punishment.console.commands",  Util.newList("eco take {player} 100", "mail send {player} You lost $100 due to logging out during combat!"));
        PUNISH_SUDO_LOGGERS = get(config, "punishment.sudo loggers.enable", false);
        PUNISH_COMMANDS_LOGGERS = get(config, "punishment.sudo loggers.commands", Util.newList("say I logged out of combat!"));
        save(config, FILEC);
    }

    //Start Language
    public static String MESSAGE_PREFIX = "";
    public static String MESSAGE_PREFIX_EXPANSION = "";
    public static String MESSAGE_ATTACK = "";
    public static String MESSAGE_TARGET = "";
    public static String MESSAGE_ATTACK_MOB = "";
    public static String MESSAGE_TARGET_MOB = "";
    public static String MESSAGE_EXPIRE = "";
    public static String MESSAGE_ENEMY_DEATH = "";
    public static String MESSAGE_BLOCKED_COMMAND = "";
    public static String MESSAGE_STILL_IN_COMBAT = "";
    public static String MESSAGE_NOT_PLAYER = "";
    public static String MESSAGE_NOT_IN_COMBAT = "";
    public static String MESSAGE_QUIT = "";
    public static String MESSAGE_FAIL = "";
    public static String MESSAGE_ACTION_BAR = "";
    public static String MESSAGE_BOSS_BAR = "";
    public static String MESSAGE_RELOAD_CONFIG = "";
    public static String MESSAGE_OPEN_INVENTORY = "";
    public static String MESSAGE_NO_ENTRY = "";
    public static String MESSAGE_NO_TELEPORT = "";
    public static String MESSAGE_SCOREBOARD_TITLE = "";
    public static List<String> SCOREBOARD_LIST = Util.newList();

    private static void defaultsL(YamlConfiguration config) {
        MESSAGE_PREFIX = get(config, "prefix.normal", "&e[&fCombatLogX&e] &f");
        MESSAGE_PREFIX_EXPANSION = get(config, "prefix.expansion", "&e[&fCombatLogX - &b{expansion}&e] &f");
        MESSAGE_ACTION_BAR = get(config, "action bar", "&3Combat &7>> {bars_left}{bars_right} &2{time_left} seconds");
        MESSAGE_BOSS_BAR = get(config, "boss bar", "&3Combat &7>> &c{time_left} seconds");
        MESSAGE_RELOAD_CONFIG = get(config, "reload config", "Reloaded 'combat.yml' and 'language.yml'");
        MESSAGE_NOT_PLAYER = get(config, "command.not player", "You are not a Player!");
        MESSAGE_TARGET = get(config, "combat.target.player", "{attacker} attacked you! You are now in combat.");
        MESSAGE_TARGET_MOB = get(config, "combat.target.entity", "You were attacked by a mob named {attacker}! You are now in combat.");
        MESSAGE_ATTACK = get(config, "combat.attack.player", "You attacked {target}! You are now in combat!");
        MESSAGE_ATTACK_MOB = get(config, "combat.attack.entity", "You attacked a mob named {target}! You are now in combat!");
        MESSAGE_EXPIRE = get(config, "combat.expire", "You are no longer in combat!");
        MESSAGE_ENEMY_DEATH = get(config, "combat.enemy death", "Your enemy called &a{enemy_name}&r has died! You are no longer in combat.");
        MESSAGE_OPEN_INVENTORY = get(config, "combat.open inventory", "You cannot open storage blocks during combat!");
        MESSAGE_BLOCKED_COMMAND = get(config, "combat.blocked command", "&eYou cannot do &c{command}&e during combat!");
        MESSAGE_NO_ENTRY = get(config, "combat.no entry", "You cannot enter a safe-zone while you are in combat!");
        MESSAGE_NO_TELEPORT = get(config, "combat.no teleport", "You cannot teleport during combat.");
        MESSAGE_STILL_IN_COMBAT = get(config, "combat.in", "You are still in combat for {time_left} seconds");
        MESSAGE_NOT_IN_COMBAT = get(config, "combat.out", "You are not in combat!");
        MESSAGE_FAIL = get(config, "combat.fail", "That person is in a No-PvP area!");
        MESSAGE_QUIT = get(config, "combat.quit", "{player} left during combat!");
        MESSAGE_SCOREBOARD_TITLE = get(config, "scoreboard.title", "&2CombatLogX");
        SCOREBOARD_LIST = get(config, "scoreboard.list", Util.newList("Time Left: {time_left}", "Enemy: {enemy_name}", "Enemy Health: {enemy_health}"));
        save(config, FILEL);
    }
}