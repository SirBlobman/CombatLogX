package com.github.sirblobman.combatlogx.api.placeholder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import me.clip.placeholderapi.PlaceholderAPI;

public final class PlaceholderHelper {
    public static String getEnemyName(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(enemy);
    }

    public static String getUnknownEnemy(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String message = languageManager.getMessageString(player, "placeholder.unknown-enemy");
        return MessageUtility.color(message);
    }

    public static String replacePlaceholderAPI(Player player, String string) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }

        return string;
    }
}
