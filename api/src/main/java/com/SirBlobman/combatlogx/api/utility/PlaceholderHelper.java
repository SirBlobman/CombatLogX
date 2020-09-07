package com.SirBlobman.combatlogx.api.utility;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.nms.EntityHandler;
import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.utility.MessageUtility;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.ICombatManager;

public final class PlaceholderHelper {
    public static String getTimeLeft(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        int secondsLeft = combatManager.getTimerLeftSeconds(player);
        if(secondsLeft > 0) return Integer.toString(secondsLeft);

        LanguageManager languageManager = plugin.getLanguageManager();
        return languageManager.getMessageColored(player, "placeholder.time-left-zero");
    }

    public static String getTimeLeftDecimal(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        ICombatManager combatManager = plugin.getCombatManager();
        double millisLeft = combatManager.getTimerLeftMillis(player);
        if(millisLeft <= 0.0D) return languageManager.getMessageColored(player, "placeholder.time-left-zero");

        double secondsLeft = (millisLeft / 1_000.0D);
        DecimalFormat decimalFormat = getDecimalFormat(plugin, player);
        return decimalFormat.format(secondsLeft);
    }

    public static String getInCombat(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean inCombat = combatManager.isInCombat(player);
        return languageManager.getMessageColored(player, ("placeholder.status." + (inCombat ? "in-combat" : "not-in-combat")));
    }

    public static String getStatus(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LanguageManager languageManager = plugin.getLanguageManager();
        boolean inCombat = combatManager.isInCombat(player);
        return languageManager.getMessageColored(player, ("placeholder.status." + (inCombat ? "fighting" : "idle")));
    }

    public static String getEnemyName(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        if(enemy == null) return getUnkownEnemy(plugin, player);

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(enemy);
    }

    public static String getEnemyHealth(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        if(enemy == null) return getUnkownEnemy(plugin, player);

        double enemyHealth = enemy.getHealth();
        DecimalFormat decimalFormat = getDecimalFormat(plugin, player);
        return decimalFormat.format(enemyHealth);
    }

    public static String getEnemyHealthRounded(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        if(enemy == null) return getUnkownEnemy(plugin, player);

        double enemyHealth = enemy.getHealth();
        long enemyHealthRounded = Math.round(enemyHealth);
        return Long.toString(enemyHealthRounded);
    }

    public static String getEnemyHearts(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        if(enemy == null) return getUnkownEnemy(plugin, player);

        double enemyHealth = enemy.getHealth();
        double enemyHearts = (enemyHealth / 2.0D);
        int enemyHeartsRounded = (int) Math.round(enemyHearts);

        char heartSymbol = '\u2764';
        char[] charArray = new char[enemyHeartsRounded];
        Arrays.fill(charArray, heartSymbol);

        String hearts = new String(charArray);
        return MessageUtility.color("&4" + hearts);
    }

    private static DecimalFormat getDecimalFormat(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String decimalFormatString = languageManager.getMessage(player, "decimal-format");
        return new DecimalFormat(decimalFormatString);
    }

    private static String getUnkownEnemy(ICombatLogX plugin, Player player) {
        LanguageManager languageManager = plugin.getLanguageManager();
        return languageManager.getMessageColored(player, "placeholder.unknown-enemy");
    }
}
