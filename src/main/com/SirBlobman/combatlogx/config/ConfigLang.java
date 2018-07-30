package com.SirBlobman.combatlogx.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.utility.Util;

public class ConfigLang extends Config {
    private static final File FILE = new File(FOLDER, "language.yml");
    private static YamlConfiguration CONFIG = new YamlConfiguration();

    public static YamlConfiguration load() {
        try {
            CONFIG = Config.load(FILE);
            defaults();
        } catch (Throwable ex) {
            String error = "Failed to load '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
        return CONFIG;
    }

    public static void save() {
        try {
            Config.save(CONFIG, FILE);
        } catch (Throwable ex) {
            String error = "Failed to save '" + FILE + "': ";
            Util.print(error);
            ex.printStackTrace();
        }
    }

    public static String MESSAGE_PREFIX = "";
    public static String MESSAGE_PREFIX_EXPANSION = "";
    public static String MESSAGE_ATTACK = "";
    public static String MESSAGE_TARGET = "";
    public static String MESSAGE_ATTACK_MOB = "";
    public static String MESSAGE_TARGET_MOB = "";
    public static String MESSAGE_EXPIRE = "";
    public static String MESSAGE_ENEMY_DEATH_PLAYER = "";
    public static String MESSAGE_ENEMY_DEATH_MOB = "";
    public static String MESSAGE_BLOCKED_COMMAND = "";
    public static String MESSAGE_STILL_IN_COMBAT = "";
    public static String MESSAGE_NOT_PLAYER = "";
    public static String MESSAGE_NO_PERMISSION = "";
    public static String MESSAGE_INVALID_TARGET = "";
    public static String MESSAGE_NOT_IN_COMBAT = "";
    public static String MESSAGE_QUIT = "";
    public static String MESSAGE_FAIL = "";
    public static String MESSAGE_ACTION_BAR = "";
    public static String MESSAGE_BOSS_BAR = "";
    public static String MESSAGE_RELOAD_CONFIG = "";
    public static String MESSAGE_OPEN_INVENTORY = "";
    public static String MESSAGE_NO_ENTRY = "";
    public static String MESSAGE_NO_TELEPORT = "";
    public static String MESSAGE_LOG_TARGET_ONLY = "";
    public static String MESSAGE_LOG_ATTACKER_ONLY = "";
    public static String MESSAGE_LOG_COMBAT = "";
    public static String MESSAGE_FORCE_UNTAG = "";
    public static String MESSAGE_FORCE_TAG = "";

    private static void defaults() {
        MESSAGE_PREFIX = get("prefix.normal", "&e[&fCombatLogX&e] &f");
        MESSAGE_PREFIX_EXPANSION = get("prefix.expansion", "&e[&fCombatLogX - &b{expansion}&e] &f");
        MESSAGE_ACTION_BAR = get("action bar", "&3Combat &7>> {bars_left}{bars_right} &2{time_left} seconds");
        MESSAGE_BOSS_BAR = get("boss bar", "&3Combat &7>> &c{time_left} seconds");
        MESSAGE_RELOAD_CONFIG = get("reload config", "Reloaded 'combat.yml' and 'language.yml'");
        MESSAGE_NOT_PLAYER = get("command.not player", "You are not a Player!");
        MESSAGE_NO_PERMISSION = get("command.no permission", "You don't have permission to do that command!");
        MESSAGE_INVALID_TARGET = get("command.invalid target", "{target} is not a Player or is offline.");
        MESSAGE_FORCE_UNTAG = get("command.combatlogx.force untag", "You removed {target} from combat.");
        MESSAGE_FORCE_TAG = get("command.combatlogx.force tag", "You put {target} into combat. This may cause bugs!");
        MESSAGE_TARGET = get("combat.target.player", "{attacker} attacked you! You are now in combat.");
        MESSAGE_TARGET_MOB = get("combat.target.entity", "You were attacked by a mob named {attacker}! You are now in combat.");
        MESSAGE_ATTACK = get("combat.attack.player", "You attacked {target}! You are now in combat!");
        MESSAGE_ATTACK_MOB = get("combat.attack.entity", "You attacked a mob named {target}! You are now in combat!");
        MESSAGE_EXPIRE = get("combat.expire", "You are no longer in combat!");
        MESSAGE_ENEMY_DEATH_PLAYER = get("combat.enemy death.player", "Your enemy called &a{enemy_name}&r has died! You are no longer in combat.");
        MESSAGE_ENEMY_DEATH_MOB = get("combat.enemy death.mob", "Your enemy called &a{enemy_name}&r has died! You are no longer in combat.");
        MESSAGE_OPEN_INVENTORY = get("combat.open inventory", "You cannot open storage blocks during combat!");
        MESSAGE_BLOCKED_COMMAND = get("combat.blocked command", "&eYou cannot do &c{command}&e during combat!");
        MESSAGE_NO_ENTRY = get("combat.no entry", "You cannot enter a safe-zone while you are in combat!");
        MESSAGE_NO_TELEPORT = get("combat.no teleport", "You cannot teleport during combat.");
        MESSAGE_STILL_IN_COMBAT = get("combat.in", "You are still in combat for {time_left} seconds");
        MESSAGE_NOT_IN_COMBAT = get("combat.out", "You are not in combat!");
        MESSAGE_FAIL = get("combat.fail", "That person is in a No-PvP area!");
        MESSAGE_QUIT = get("combat.quit", "{player} left during combat!");
        MESSAGE_LOG_ATTACKER_ONLY = get("logger.attacker only", "{attacker} was placed into combat by an unknown source (expansion?)");
        MESSAGE_LOG_TARGET_ONLY = get("logger.target only", "{target} was placed into combat by an unknown source (expansion?)");
        MESSAGE_LOG_COMBAT = get("logger.combat", "{target} was attacked by {attacker}");
        save();
    }

    private static <T> T get(String path, T defaultValue) {
        return Config.get(CONFIG, path, defaultValue);
    }
}