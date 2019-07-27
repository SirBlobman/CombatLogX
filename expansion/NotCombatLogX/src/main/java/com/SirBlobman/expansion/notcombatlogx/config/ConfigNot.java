package com.SirBlobman.expansion.notcombatlogx.config;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.expansion.notcombatlogx.NotCombatLogX;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.io.File;

public class ConfigNot extends Config {
    private static YamlConfiguration config = new YamlConfiguration();

    public static void load() {
        File folder = NotCombatLogX.FOLDER;
        File file = new File(folder, "not.yml");

        if (!file.exists()) copyFromJar("not.yml", folder);
        config = load(file);
    }

    public static boolean canDamageTypeTagPlayer(DamageCause dc) {
        load();
        if (dc == null) return false;
        else {
            boolean allDamage = get(config, "all damage", true);
            if (allDamage) return true;
            else {
                String name = dc.name().toLowerCase().replace("_", " ");
                String path = "damage type." + name;
                return get(config, path, false);
            }
        }
    }

    public static String getTagMessage(DamageCause dc) {
        if (canDamageTypeTagPlayer(dc)) {
            boolean allDamage = get(config, "all damage", true);
            if (allDamage) {
                return ConfigLang.getWithPrefix("messages.expansions.notcombatlogx.all damage");
            } else {
                String name = dc.name().toLowerCase().replace("_", " ");
                String path = "messages.expansions.notcombatlogx.damage tag." + name;
                return ConfigLang.getWithPrefix(path);
            }
        } else return "";
    }
}