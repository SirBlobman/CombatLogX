package com.github.sirblobman.combatlogx.placeholder;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;

import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.Nullable;

import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyDisplayName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealth;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHealthRounded;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHearts;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyHeartsCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyName;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyType;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyWorld;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyX;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyY;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getEnemyZ;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getInCombat;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getPunishmentCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getStatus;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTagCount;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeft;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftDecimal;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftDecimalSpecific;
import static com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper.getTimeLeftSpecific;

public final class BasePlaceholderExpansion implements IPlaceholderExpansion {
    private final ICombatLogX plugin;

    public BasePlaceholderExpansion(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    @Override
    public ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @Override
    public String getId() {
        return "combatlogx";
    }

    @Override
    public String getReplacement(Player player, List<Entity> enemyList, String placeholder) {
        ICombatLogX plugin = getCombatLogX();
        switch(placeholder) {
            case "player": return player.getName();
            case "tag_count": return getTagCount(plugin, player);
            case "enemy_count": return getEnemyCount(plugin, player);
            case "time_left": return getTimeLeft(plugin, player);
            case "time_left_decimal": return getTimeLeftDecimal(plugin, player);
            case "in_combat": return getInCombat(plugin, player);
            case "status": return getStatus(plugin, player);
            case "punishment_count": return getPunishmentCount(plugin, player);
            default: break;
        }

        if(placeholder.startsWith("time_left_")) {
            if(placeholder.startsWith("time_left_decimal_")) {
                String numberString = placeholder.substring("time_left_decimal_".length());
                try {
                    int index = (Integer.parseInt(numberString) - 1);
                    return getTimeLeftDecimalSpecific(plugin, player, index);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }

            String numberString = placeholder.substring("time_left_".length());
            try {
                int index = (Integer.parseInt(numberString) - 1);
                return getTimeLeftSpecific(plugin, player, index);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        if(placeholder.startsWith("current_enemy_")) {
            Entity currentEnemy = getSpecificEnemy(enemyList, 0);
            String enemyPlaceholder = placeholder.substring("current_enemy_".length());
            return getEnemyPlaceholder(plugin, player, currentEnemy, enemyPlaceholder);
        }

        if(placeholder.startsWith("specific_enemy_")) {
            String subPlaceholder = placeholder.substring("specific_enemy_".length());
            int nextUnderscore = subPlaceholder.indexOf('_');
            if (nextUnderscore == -1) {
                return null;
            }

            int enemyIndex;
            try {
                String enemyIdString = subPlaceholder.substring(0, nextUnderscore);
                enemyIndex = (Integer.parseInt(enemyIdString) - 1);
            } catch(NumberFormatException ex) {
                return null;
            }

            Entity specificEnemy = getSpecificEnemy(enemyList, enemyIndex);
            String enemyPlaceholder = subPlaceholder.substring(nextUnderscore + 1);
            return getEnemyPlaceholder(plugin, player, specificEnemy, enemyPlaceholder);
        }

        return null;
    }

    @Nullable
    private String getEnemyPlaceholder(ICombatLogX plugin, Player player, Entity enemy, String placeholder) {
        switch(placeholder) {
            case "name": return getEnemyName(plugin, player, enemy);
            case "type": return getEnemyType(plugin, player, enemy);
            case "display_name": return getEnemyDisplayName(plugin, player, enemy);
            case "health": return getEnemyHealth(plugin, player, enemy);
            case "health_rounded": return getEnemyHealthRounded(plugin, player, enemy);
            case "hearts": return getEnemyHearts(plugin, player, enemy);
            case "hearts_count": return getEnemyHeartsCount(plugin, player, enemy);
            case "world": return getEnemyWorld(plugin, player, enemy);
            case "x": return getEnemyX(plugin, player, enemy);
            case "y": return getEnemyY(plugin, player, enemy);
            case "z": return getEnemyZ(plugin, player, enemy);
            default: break;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        if(pluginManager.isPluginEnabled("PlaceholderAPI") && enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            String placeholderString = "{" + placeholder + "}";
            return PlaceholderAPI.setBracketPlaceholders(enemyPlayer, placeholderString);
        }

        return null;
    }

    @Nullable
    private Entity getSpecificEnemy(List<Entity> enemyList, int index) {
        if(enemyList.isEmpty()) {
            return null;
        }

        int enemyListSize = enemyList.size();
        if(index >= enemyListSize) {
            return null;
        }

        return enemyList.get(index);
    }
}
