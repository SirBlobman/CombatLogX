package com.github.sirblobman.combatlogx.placeholder;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.format.NamedTextColor;
import com.github.sirblobman.api.shaded.adventure.text.minimessage.MiniMessage;
import com.github.sirblobman.api.shaded.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;

import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasePlaceholderExpansion implements IPlaceholderExpansion {
    private final ICombatLogX plugin;

    public BasePlaceholderExpansion(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @Override
    public @NotNull String getId() {
        return "combatlogx";
    }

    @Override
    public @Nullable Component getReplacement(@NotNull Player player, @NotNull List<Entity> enemyList,
                                              @NotNull String placeholder) {
        switch (placeholder) {
            case "enemy_count":
                return getEnemyCount(player);
            case "in_combat":
                return getInCombat(player);
            case "player":
                return Component.text(player.getName());
            case "punishment_count":
                return getPunishmentCount(player);
            case "status":
                return getStatus(player);
            case "tag_count":
                return getTagCount(player);
            case "time_left":
                return getTimeLeft(player);
            case "time_left_decimal":
                return getTimeLeftDecimal(player);
            default:
                break;
        }

        if (placeholder.startsWith("time_left_")) {
            if (placeholder.startsWith("time_left_decimal_")) {
                String numberString = placeholder.substring("time_left_decimal_".length());
                try {
                    int index = (Integer.parseInt(numberString) - 1);
                    return getTimeLeftDecimalSpecific(player, index);
                } catch (NumberFormatException ex) {
                    return null;
                }
            }

            String numberString = placeholder.substring("time_left_".length());
            try {
                int index = (Integer.parseInt(numberString) - 1);
                return getTimeLeftSpecific(player, index);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        if (placeholder.startsWith("current_enemy_")) {
            Entity currentEnemy = getSpecificEnemy(enemyList, 0);
            String enemyPlaceholder = placeholder.substring("current_enemy_".length());
            return getEnemyPlaceholder(player, currentEnemy, enemyPlaceholder);
        }

        if (placeholder.startsWith("specific_enemy_")) {
            String subPlaceholder = placeholder.substring("specific_enemy_".length());
            int nextUnderscore = subPlaceholder.indexOf('_');
            if (nextUnderscore == -1) {
                return null;
            }

            int enemyIndex;
            try {
                String enemyIdString = subPlaceholder.substring(0, nextUnderscore);
                enemyIndex = (Integer.parseInt(enemyIdString) - 1);
            } catch (NumberFormatException ex) {
                return null;
            }

            Entity specificEnemy = getSpecificEnemy(enemyList, enemyIndex);
            String enemyPlaceholder = subPlaceholder.substring(nextUnderscore + 1);
            return getEnemyPlaceholder(player, specificEnemy, enemyPlaceholder);
        }

        return null;
    }

    private @Nullable Component getEnemyPlaceholder(@NotNull Player player, @Nullable Entity enemy,
                                                    @NotNull String placeholder) {
        if (enemy == null) {
            return getUnknownEnemy(player);
        }

        switch (placeholder) {
            case "name":
                return getEnemyName(enemy);
            case "type":
                return getEnemyType(enemy);
            case "display_name":
                return getEnemyDisplayName(enemy);
            case "health":
                return getEnemyHealth(player, enemy);
            case "health_rounded":
                return getEnemyHealthRounded(enemy);
            case "hearts":
                return getEnemyHearts(enemy);
            case "hearts_count":
                return getEnemyHeartsCount(enemy);
            case "world":
                return getEnemyWorld(enemy);
            case "x":
                return getEnemyX(enemy);
            case "y":
                return getEnemyY(enemy);
            case "z":
                return getEnemyZ(enemy);
            default:
                break;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PlaceholderAPI") && enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            if (enemyPlayer.isOnline()) {
                return getEnemyPlaceholderAPI(enemyPlayer, placeholder);
            }
        }

        return null;
    }

    private @Nullable Entity getSpecificEnemy(@NotNull List<Entity> enemyList, int index) {
        if (enemyList.isEmpty()) {
            return null;
        }

        int enemyListSize = enemyList.size();
        if (index >= enemyListSize) {
            return null;
        }

        return enemyList.get(index);
    }

    private @NotNull Component getEnemyCount(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return Component.text(0);
        }

        List<UUID> enemyIdList = tagInformation.getEnemyIds();
        int enemyIdListSize = enemyIdList.size();
        return Component.text(enemyIdListSize);
    }

    private @NotNull Component getInCombat(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        boolean inCombat = combatManager.isInCombat(player);

        LanguageManager languageManager = combatLogX.getLanguageManager();
        String keyPart = ((inCombat ? "" : "not-") + "in-combat");
        String fullKey = ("placeholder.status." + keyPart);
        return languageManager.getMessage(player, fullKey);
    }

    private @NotNull Component getPunishmentCount(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        if (punishConfiguration.isEnablePunishmentCounter()) {
            IPunishManager punishManager = combatLogX.getPunishManager();
            long punishmentCount = punishManager.getPunishmentCount(player);
            return Component.text(punishmentCount);
        }

        return Component.text(0);
    }

    private @NotNull Component getStatus(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        boolean inCombat = combatManager.isInCombat(player);

        LanguageManager languageManager = combatLogX.getLanguageManager();
        String keyPart = (inCombat ? "fighting" : "idle");
        String fullKey = ("placeholder.status." + keyPart);
        return languageManager.getMessage(player, fullKey);
    }

    private @NotNull Component getTagCount(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return Component.text(0);
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        return Component.text(tagListSize);
    }

    private @NotNull Component getTimeLeft(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        Component zero = languageManager.getMessage(player, "placeholder.time-left-zero");

        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return zero;
        }

        long expireMillis = tagInformation.getExpireMillisCombined();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        long timeLeftMillis = Math.max(0L, subtractMillis);
        if (timeLeftMillis == 0L) {
            return zero;
        }

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        if (secondsLeft <= 0L) {
            return zero;
        }

        return Component.text(secondsLeft);
    }

    private @NotNull Component getTimeLeftSpecific(@NotNull Player player, int index) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        Component zero = languageManager.getMessage(player, "placeholder.time-left-zero");

        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return zero;
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        if (index < 0 || index >= tagListSize) {
            return zero;
        }

        CombatTag combatTag = tagList.get(index);
        long expireMillis = combatTag.getExpireMillis();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        long timeLeftMillis = Math.max(0L, subtractMillis);
        if (timeLeftMillis == 0L) {
            return zero;
        }

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        if (secondsLeft <= 0L) {
            return zero;
        }

        return Component.text(secondsLeft);
    }

    private @NotNull Component getTimeLeftDecimal(@NotNull Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        Component zero = languageManager.getMessage(player, "placeholder.time-left-zero");

        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return zero;
        }

        long expireMillis = tagInformation.getExpireMillisCombined();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        double timeLeftMillis = Math.max(0.0D, subtractMillis);
        if (timeLeftMillis <= 0.0D) {
            return zero;
        }

        double secondsLeft = (timeLeftMillis / 1_000.0D);
        if (secondsLeft <= 0.0D) {
            return zero;
        }

        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);
        String timeLeftString = decimalFormat.format(secondsLeft);
        return Component.text(timeLeftString);
    }

    private @NotNull Component getTimeLeftDecimalSpecific(@NotNull Player player, int index) {
        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        Component zero = languageManager.getMessage(player, "placeholder.time-left-zero");

        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return zero;
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        if (index < 0 || index >= tagListSize) {
            return zero;
        }

        CombatTag combatTag = tagList.get(index);
        long expireMillis = combatTag.getExpireMillis();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        double timeLeftMillis = Math.max(0.0D, subtractMillis);
        if (timeLeftMillis <= 0.0D) {
            return zero;
        }

        double secondsLeft = (timeLeftMillis / 1_000.0D);
        if (secondsLeft <= 0.0D) {
            return zero;
        }

        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);
        String timeLeftString = decimalFormat.format(secondsLeft);
        return Component.text(timeLeftString);
    }

    private @NotNull Component getUnknownEnemy(@NotNull Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        return languageManager.getMessage(player, "placeholder.unknown-enemy");
    }

    private @NotNull Component getEnemyName(@NotNull Entity entity) {
        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        String entityName = entityHandler.getName(entity);
        return Component.text(entityName);
    }

    private @NotNull Component getEnemyDisplayName(@NotNull Entity enemy) {
        if (PaperChecker.hasNativeComponentSupport()) {
            Component customName = PaperHelper.getCustomName(enemy);
            if (customName != null) {
                return customName;
            }
        }

        return getEnemyName(enemy);
    }

    private @NotNull Component getEnemyType(@NotNull Entity enemy) {
        EntityType entityType = enemy.getType();
        String entityTypeName = entityType.name();
        return Component.text(entityTypeName);
    }

    private @NotNull Component getEnemyHealth(@NotNull Player player, @NotNull Entity enemy) {
        double enemyHealth = 0.0D;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        }

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        DecimalFormat decimalFormat = languageManager.getDecimalFormat(player);

        String healthString = decimalFormat.format(enemyHealth);
        return Component.text(healthString);
    }

    private @NotNull Component getEnemyHealthRounded(@NotNull Entity enemy) {
        double enemyHealth = 0.0D;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        }

        long round = Math.round(enemyHealth);
        return Component.text(round);
    }

    private @NotNull Component getEnemyHearts(@NotNull Entity enemy) {
        double enemyHealth = 0.0D;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        }

        double heartsDecimal = (enemyHealth / 2.0D);
        int hearts = (int) Math.round(Math.floor(heartsDecimal));
        if (hearts > 10) {
            return Component.text(hearts);
        }

        char symbol = '❤';
        char[] symbols = new char[hearts];
        Arrays.fill(symbols, symbol);

        String heartsString = new String(symbols);
        return Component.text(heartsString, NamedTextColor.RED);
    }

    private @NotNull Component getEnemyHeartsCount(@NotNull Entity enemy) {
        double enemyHealth = 0.0D;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        }

        double heartsDecimal = (enemyHealth / 2.0D);
        long hearts = Math.round(Math.floor(heartsDecimal));
        return Component.text(hearts);
    }

    private @NotNull Component getEnemyWorld(@NotNull Entity enemy) {
        World world = enemy.getWorld();
        String worldName = world.getName();
        return Component.text(worldName);
    }

    private @NotNull Component getEnemyX(@NotNull Entity enemy) {
        Location location = enemy.getLocation();
        int blockX = location.getBlockX();
        return Component.text(blockX);
    }

    private @NotNull Component getEnemyY(@NotNull Entity enemy) {
        Location location = enemy.getLocation();
        int blockY = location.getBlockY();
        return Component.text(blockY);
    }

    private @NotNull Component getEnemyZ(@NotNull Entity enemy) {
        Location location = enemy.getLocation();
        int blockZ = location.getBlockZ();
        return Component.text(blockZ);
    }

    private @NotNull Component getEnemyPlaceholderAPI(@NotNull Player enemy, @NotNull String placeholder) {
        String placeholderString = ("{" + placeholder + "}");
        String replacement = PlaceholderAPI.setBracketPlaceholders(enemy, placeholderString);

        if (replacement.contains("&")) {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
            return serializer.deserialize(replacement);
        }

        if (replacement.contains("§")) {
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
            return serializer.deserialize(replacement);
        }

        ICombatLogX combatLogX = getCombatLogX();
        LanguageManager languageManager = combatLogX.getLanguageManager();
        MiniMessage miniMessage = languageManager.getMiniMessage();
        return miniMessage.deserialize(replacement);
    }
}
