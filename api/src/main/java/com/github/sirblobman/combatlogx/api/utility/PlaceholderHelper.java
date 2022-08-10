package com.github.sirblobman.combatlogx.api.utility;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.Nullable;

public final class PlaceholderHelper {
    @Nullable
    public static Entity getCurrentEnemy(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return null;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        if (enemyList.isEmpty()) {
            return null;
        }

        return enemyList.get(0);
    }

    @Nullable
    public static Entity getSpecificEnemy(ICombatLogX plugin, Player player, int index) {
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return null;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        if (enemyList.isEmpty()) {
            return null;
        }

        int enemyListSize = enemyList.size();
        if (index < 0 || index >= enemyListSize) {
            return null;
        }

        return enemyList.get(index);
    }

    public static String getPunishmentCount(ICombatLogX plugin, Player player) {
        IPunishManager punishManager = plugin.getPunishManager();
        long punishmentCount = punishManager.getPunishmentCount(player);
        return Long.toString(punishmentCount);
    }

    public static String getTimeLeft(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String zeroString = languageManager.getMessageString(player, "placeholder.time-left-zero", null);
        String zeroMessage = MessageUtility.color(zeroString);

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return zeroMessage;
        }

        long expireMillis = tagInformation.getExpireMillisCombined();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        long timeLeftMillis = Math.max(0L, subtractMillis);
        if (timeLeftMillis == 0L) {
            return zeroMessage;
        }

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        if (secondsLeft <= 0L) {
            return zeroMessage;
        }

        return Long.toString(secondsLeft);
    }

    public static String getTimeLeftSpecific(ICombatLogX plugin, Player player, int index) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String zeroString = languageManager.getMessageString(player, "placeholder.time-left-zero", null);
        String zeroMessage = MessageUtility.color(zeroString);

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return zeroMessage;
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        if (index < 0 || index >= tagListSize) {
            return zeroMessage;
        }

        CombatTag combatTag = tagList.get(index);
        long expireMillis = combatTag.getExpireMillis();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        long timeLeftMillis = Math.max(0L, subtractMillis);
        if (timeLeftMillis <= 0L) {
            return zeroMessage;
        }

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        if (secondsLeft <= 0L) {
            return zeroMessage;
        }

        return Long.toString(secondsLeft);
    }

    public static String getTimeLeftDecimalSpecific(ICombatLogX plugin, Player player, int index) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String zeroString = languageManager.getMessageString(player, "placeholder.time-left-zero", null);
        String zeroMessage = MessageUtility.color(zeroString);

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return zeroMessage;
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        if (index < 0 || index >= tagListSize) {
            return zeroMessage;
        }

        CombatTag combatTag = tagList.get(index);
        long expireMillis = combatTag.getExpireMillis();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        double timeLeftMillis = Math.max(0.0D, subtractMillis);
        if (timeLeftMillis <= 0.0D) {
            return zeroMessage;
        }

        double secondsLeft = (timeLeftMillis / 1_000.0D);
        if (secondsLeft <= 0.0D) {
            return zeroMessage;
        }

        DecimalFormat decimalFormat = getDecimalFormat(plugin, player);
        return decimalFormat.format(secondsLeft);
    }

    public static String getTimeLeftDecimal(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        Component zeroComponent = languageManager.getMessage(player, "placeholder.time-left-zero", null);
        String zeroMessage = ComponentHelper.toLegacy(zeroComponent);

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return zeroMessage;
        }

        long expireMillis = tagInformation.getExpireMillisCombined();
        long systemMillis = System.currentTimeMillis();
        long subtractMillis = (expireMillis - systemMillis);
        double timeLeftMillis = Math.max(0.0D, subtractMillis);
        if (timeLeftMillis <= 0.0D) {
            return zeroMessage;
        }

        double secondsLeft = (timeLeftMillis / 1_000.0D);
        if (secondsLeft <= 0.0D) {
            return zeroMessage;
        }

        DecimalFormat decimalFormat = getDecimalFormat(plugin, player);
        return decimalFormat.format(secondsLeft);
    }

    public static String getInCombat(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean inCombat = combatManager.isInCombat(player);

        String key = ("placeholder.status." + (inCombat ? "in-combat" : "not-in-combat"));
        String message = languageManager.getMessageString(player, key, null);
        return MessageUtility.color(message);
    }

    public static String getStatus(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean inCombat = combatManager.isInCombat(player);

        String key = ("placeholder.status." + (inCombat ? "fighting" : "idle"));
        String message = languageManager.getMessageString(player, key, null);
        return MessageUtility.color(message);
    }

    public static String getEnemyType(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        EntityType entityType = enemy.getType();
        return entityType.name();
    }

    public static String getEnemyName(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(enemy);
    }

    public static String getEnemyDisplayName(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        if (enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            return enemyPlayer.getDisplayName();
        }

        return getEnemyName(plugin, player, enemy);
    }

    public static String getEnemyHealth(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        double enemyHealth;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        } else {
            enemyHealth = 0.0D;
        }

        DecimalFormat decimalFormat = getDecimalFormat(plugin, player);
        return decimalFormat.format(enemyHealth);
    }

    public static String getEnemyHealthRounded(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        double enemyHealth;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        } else {
            enemyHealth = 0.0D;
        }

        long enemyHealthRounded = Math.round(enemyHealth);
        return Long.toString(enemyHealthRounded);
    }

    public static String getEnemyHeartsCount(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        double enemyHealth;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        } else {
            enemyHealth = 0.0D;
        }

        double enemyHearts = (enemyHealth / 2.0D);
        long enemyHeartsRounded = Math.round(enemyHearts);
        return Long.toString(enemyHeartsRounded);
    }

    public static String getEnemyHearts(ICombatLogX plugin, Player player, Entity enemy) {
        if (enemy == null) {
            return getUnknownEnemy(plugin, player);
        }

        double enemyHealth;
        if (enemy instanceof LivingEntity) {
            enemyHealth = ((LivingEntity) enemy).getHealth();
        } else {
            enemyHealth = 0.0D;
        }

        double enemyHearts = (enemyHealth / 2.0D);
        long enemyHeartsRounded = Math.round(enemyHearts);
        if (enemyHeartsRounded > 10L) {
            return Long.toString(enemyHeartsRounded);
        }

        char heartSymbol = '\u2764';
        char[] charArray = new char[(int) enemyHeartsRounded];
        Arrays.fill(charArray, heartSymbol);

        String hearts = new String(charArray);
        return MessageUtility.color("&4" + hearts);
    }

    public static String getUnknownEnemy(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String message = languageManager.getMessageString(player, "placeholder.unknown-enemy", null);
        return MessageUtility.color(message);
    }

    public static DecimalFormat getDecimalFormat(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        Language language = languageManager.getLanguage(player);
        if (language == null) {
            DecimalFormatSymbols usSymbols = DecimalFormatSymbols.getInstance(Locale.US);
            return new DecimalFormat("0.00", usSymbols);
        }

        return language.getDecimalFormat();
    }

    @Nullable
    public static Location getEnemyLocation(Entity enemy) {
        return (enemy == null ? null : enemy.getLocation());
    }

    public static String getEnemyWorld(ICombatLogX plugin, Player player, Entity enemy) {
        Location location = getEnemyLocation(enemy);
        if (location == null) {
            return getUnknownEnemy(plugin, player);
        }

        World world = location.getWorld();
        if (world == null) {
            return getUnknownEnemy(plugin, player);
        }

        return world.getName();
    }

    public static String getEnemyX(ICombatLogX plugin, Player player, Entity enemy) {
        Location location = getEnemyLocation(enemy);
        if (location == null) {
            return getUnknownEnemy(plugin, player);
        }

        int x = location.getBlockX();
        return Integer.toString(x);
    }

    public static String getEnemyY(ICombatLogX plugin, Player player, Entity enemy) {
        Location location = getEnemyLocation(enemy);
        if (location == null) {
            return getUnknownEnemy(plugin, player);
        }

        int y = location.getBlockY();
        return Integer.toString(y);
    }

    public static String getEnemyZ(ICombatLogX plugin, Player player, Entity enemy) {
        Location location = getEnemyLocation(enemy);
        if (location == null) {
            return getUnknownEnemy(plugin, player);
        }

        int z = location.getBlockZ();
        return Integer.toString(z);
    }

    public static String getTagCount(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return Integer.toString(0);
        }

        List<CombatTag> tagList = tagInformation.getTags();
        int tagListSize = tagList.size();
        return Integer.toString(tagListSize);
    }

    public static String getEnemyCount(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null || tagInformation.isExpired()) {
            return Integer.toString(0);
        }

        List<UUID> enemyIdList = tagInformation.getEnemyIds();
        int enemyIdListSize = enemyIdList.size();
        return Integer.toString(enemyIdListSize);
    }

    @Nullable
    public static String getPlaceholder(ICombatLogX plugin, Player player, String placeholder) {
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        if (!placeholder.startsWith("newbie_helper_")) {
            placeholder = ("combatlogx_" + placeholder);
        }

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        List<Entity> enemyList = (tagInformation == null ? Collections.emptyList() : tagInformation.getEnemies());
        return placeholderManager.getPlaceholderReplacement(player, enemyList, placeholder);
    }

    public static String replacePlaceholderAPI(Player player, String string) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }

        return string;
    }
}
