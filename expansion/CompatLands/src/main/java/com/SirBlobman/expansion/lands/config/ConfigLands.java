package com.SirBlobman.expansion.lands.config;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.lands.CompatLands;

public class ConfigLands extends Config {
    public static double NO_ENTRY_KNOCKBACK_STRENGTH;
    public static int MESSAGE_COOLDOWN;
    private static YamlConfiguration config = new YamlConfiguration();
    private static String NO_ENTRY_MODE;

    public static void load() {
        File folder = CompatLands.FOLDER;
        File file = new File(folder, "lands.yml");
        if (!file.exists()) copyFromJar("lands.yml", folder);

        config = load(file);
        defaults();
    }

    private static void defaults() {
        NO_ENTRY_MODE = get(config, "lands options.no entry.mode", "KNOCKBACK");
        NO_ENTRY_KNOCKBACK_STRENGTH = get(config, "lands options.no entry.knockback strength", 1.5D);
        MESSAGE_COOLDOWN = get(config, "lands options.no entry.message cooldown", 5);
    }

    public static NoEntryMode getNoEntryMode() {
        if (NO_ENTRY_MODE == null || NO_ENTRY_MODE.isEmpty()) load();
        String mode = NO_ENTRY_MODE.toUpperCase();
        try {
            return NoEntryMode.valueOf(mode);
        } catch (Throwable ex) {
            String error = "Invalid Mode '" + NO_ENTRY_MODE + "' in 'lands.yml'. Valid modes are " + String.join(" ", Arrays.stream(NoEntryMode.values()).map(Enum::name).toArray(String[]::new));
            Util.print(error);
            return NoEntryMode.CANCEL;
        }
    }
}