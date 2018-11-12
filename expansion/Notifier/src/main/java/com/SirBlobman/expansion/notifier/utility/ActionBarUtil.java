package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.util.List;

import org.bukkit.entity.Player;

public class ActionBarUtil extends Util {
    public static void updateActionBar(Player player) {
        int timeLeft = CombatUtil.getTimeLeft(player);
        if (timeLeft > 0) {
            List<String> keys = newList("{time_left}", "{bars_left}", "{bars_right}");
            List<?> vals = newList(timeLeft, getBarsLeft(player), getBarsRight(player));
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
}