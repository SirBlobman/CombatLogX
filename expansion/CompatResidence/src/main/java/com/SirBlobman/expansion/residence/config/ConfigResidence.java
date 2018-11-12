package com.SirBlobman.expansion.residence.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.residence.CompatResidence;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigResidence extends Config {
    public static double NO_ENTRY_KNOCKBACK_STRENGTH;
    public static int NO_ENTRY_MESSAGE_COOLDOWN;
    private static YamlConfiguration config = new YamlConfiguration();
    private static String NO_ENTRY_MODE;

    public static void load() {
        File folder = CompatResidence.FOLDER;
        File file = new File(folder, "residence.yml");
        if (!file.exists()) copyFromJar("residence.yml", folder);

        config = load(file);
        defaults();
    }

    private static void defaults() {
        NO_ENTRY_MODE = get(config, "residence options.no entry.mode", "KNOCKBACK");
        NO_ENTRY_KNOCKBACK_STRENGTH = get(config, "residence options.no entry.knockback strength", 1.5D);
        NO_ENTRY_MESSAGE_COOLDOWN = get(config, "residence options.no entry.message cooldown", 5);
    }

    public static NoEntryMode getNoEntryMode() {
        if (NO_ENTRY_MODE == null || NO_ENTRY_MODE.isEmpty()) load();
        String mode = NO_ENTRY_MODE.toUpperCase();
        try {
            return NoEntryMode.valueOf(mode);
        } catch (Throwable ex) {
            String error = "Invalid Mode '" + NO_ENTRY_MODE + "' in 'residence.yml'. Valid modes are " + String.join(" ", Arrays.stream(NoEntryMode.values()).map(Enum::name).toArray(String[]::new));
            Util.print(error);
            return NoEntryMode.CANCEL;
        }
    }

    public enum NoEntryMode {CANCEL, TELEPORT, KNOCKBACK, KILL}
}