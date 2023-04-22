package com.github.sirblobman.combatlogx.api.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import me.clip.placeholderapi.PlaceholderAPI;

public final class PlaceholderHelper {
    public static @NotNull Component getEnemyName(@NotNull ICombatLogX plugin, @NotNull Player player,
                                                  @Nullable Entity entity) {
        if (entity == null) {
            return getUnknownEnemy(plugin, player);
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            Component customName = PaperHelper.getCustomName(entity);
            if (customName != null) {
                return customName;
            }
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        String entityName = entityHandler.getName(entity);
        return Component.text(entityName);
    }

    public static @NotNull Component getUnknownEnemy(@NotNull ICombatLogX plugin, @NotNull Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        return languageManager.getMessage(player, "placeholder.unknown-enemy");
    }

    public static @NotNull String replacePlaceholderAPI(@NotNull Player player, @NotNull String string) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }

        return string;
    }
}
