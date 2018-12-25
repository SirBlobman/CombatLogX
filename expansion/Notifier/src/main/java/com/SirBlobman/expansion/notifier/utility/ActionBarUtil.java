package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ActionBarUtil extends Util {
    public static void updateActionBar(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt > 0) {
            LivingEntity enemy = CombatUtil.getEnemy(player);
            String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
            String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
            String yes = ConfigLang.get("messages.expansions.placeholder compatibility.yes");
            String no = ConfigLang.get("messages.expansions.placeholder compatibility.no");
            String idling = ConfigLang.get("messages.expansions.placeholder compatibility.status.idling");
            String fighting = ConfigLang.get("messages.expansions.placeholder compatibility.status.fighting");
            
            List<String> keys = Util.newList("{time_left}", "{enemy_name}", "{enemy_health}", "{in_combat}", "{status}", "{bars_left}", "{bars_right}");
            String timeLeft = (timeLeftInt > 0) ? Integer.toString(timeLeftInt) : ConfigLang.get("messages.expansions.placeholder compatibility.zero time left");
            List<?> vals = Util.newList(timeLeft, enemyName, enemyHealth, CombatUtil.isInCombat(player) ? yes : no, CombatUtil.isInCombat(player) ? fighting : idling, getBarsLeft(player), getBarsRight(player));
            String msg = formatMessage(ConfigNotifier.ACTION_BAR_FORMAT, keys, vals);
            
            LegacyHandler.getLegacyHandler().sendActionBar(player, msg);
        } else removeActionBar(player);
    }

    public static void removeActionBar(Player player) {
        String msg = color(ConfigNotifier.ACTION_BAR_NO_LONGER_IN_COMBAT);

        LegacyHandler.getLegacyHandler().sendActionBar(player, msg);
    }

    private static String getBarsLeft(Player p) {
        int timeLeft = CombatUtil.getTimeLeft(p);
        int right = (ConfigOptions.OPTION_TIMER - timeLeft);
        int left = (ConfigOptions.OPTION_TIMER - right);

        StringBuilder color = new StringBuilder(color("&a"));
        for (int i = 0; i < left; i++) {
            color.append("|");
        }
        return color.toString();
    }

    private static String getBarsRight(Player p) {
        int timeLeft = CombatUtil.getTimeLeft(p);
        int right = (ConfigOptions.OPTION_TIMER - timeLeft);

        StringBuilder color = new StringBuilder(color("&c"));
        for (int i = 0; i < right; i++) {
            color.append("|");
        }
        return color.toString();
    }

    private static String formatDouble(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }
}