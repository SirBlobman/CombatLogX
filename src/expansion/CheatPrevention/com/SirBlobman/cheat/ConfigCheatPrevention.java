package com.SirBlobman.cheat;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigCheatPrevention extends Config {
    private static final File FOLDER = CheatPrevention.FOLDER;
    private static final File FILE = new File(FOLDER, "cheat prevention.yml");
    private static YamlConfiguration CONFIG = new YamlConfiguration();

    public static YamlConfiguration load() {
        try {
            CONFIG = load(FILE);
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

    public static boolean CHEAT_PREVENT_BLOCKED_COMMANDS_MODE = false;
    public static boolean CHEAT_PREVENT_DISABLE_FLIGHT = true;
    public static boolean CHEAT_PREVENT_ENABLE_FLIGHT = true;
    public static boolean CHEAT_PREVENT_NO_ENTRY = true;
    public static boolean CHEAT_PREVENT_CHANGE_GAMEMODE = true;
    public static boolean CHEAT_PREVENT_OPEN_INVENTORIES = true;
    public static boolean CHEAT_PREVENT_AUTO_CLOSE_GUIS = true;
    public static boolean CHEAT_PREVENT_TELEPORT = true;
    public static boolean CHEAT_PREVENT_TELEPORT_ALLOW_ENDERPEARLS = true;
    public static boolean CHEAT_PREVENT_TELEPORT_ENDERPEARLS_RESTART = true;
    public static String CHEAT_PREVENT_CHANGE_GAMEMODE_MODE = "SURVIVAL";
    public static List<String> CHEAT_PREVENT_BLOCKED_COMMANDS = Util.newList("fly", "tpa", "tpahere", "spawn", "home");
    public static List<String> CHEAT_PREVENT_BLOCKED_POTIONS = Util.newList("INVISIBILITY", "INCREASE_DAMAGE");

    private static void defaults() {
        CHEAT_PREVENT_OPEN_INVENTORIES = get("cheat prevention.inventories.prevent opening", true);
        CHEAT_PREVENT_AUTO_CLOSE_GUIS = get("cheat prevention.inventories.automatically close", true);
        CHEAT_PREVENT_TELEPORT = get("cheat prevention.teleportation.prevent", true);
        CHEAT_PREVENT_TELEPORT_ALLOW_ENDERPEARLS = get("cheat prevention.teleportation.allow ender pearls", false);
        CHEAT_PREVENT_TELEPORT_ENDERPEARLS_RESTART = get("cheat prevention.teleportation.enderpearls restart timer",
                true);
        CHEAT_PREVENT_NO_ENTRY = get("cheat prevention.safezone.no entry", true);
        CHEAT_PREVENT_DISABLE_FLIGHT = get("cheat prevention.flight.disable", true);
        CHEAT_PREVENT_ENABLE_FLIGHT = get("cheat prevention.flight.re-enable after combat", false);
        CHEAT_PREVENT_CHANGE_GAMEMODE = get("cheat prevention.change gamemode.enabled", true);
        CHEAT_PREVENT_CHANGE_GAMEMODE_MODE = get("cheat prevention.change gamemode.mode", "SURVIVAL");
        CHEAT_PREVENT_BLOCKED_COMMANDS_MODE = get("cheat prevention.blocked commands.whitelist mode", false);
        CHEAT_PREVENT_BLOCKED_COMMANDS = get("cheat prevention.blocked commands.commands",
                Util.newList("fly", "tpa", "tpahere", "spawn", "home"));
        CHEAT_PREVENT_BLOCKED_POTIONS = get("cheat prevention.blocked potions",
                Util.newList("INVISIBILITY", "INCREASE_DAMAGE"));

        save();
    }

    private static <T> T get(String path, T defaultValue) {
        return get(CONFIG, path, defaultValue);
    }
}