package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.placeholders.hook.IPlaceholderHandler;

public class ActionBarUtil extends Util {
    public static void updateActionBar(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt > 0) {
            String msg = ConfigNotifier.ACTION_BAR_FORMAT;
            if(Expansions.isEnabled("CompatPlaceholders")) {
                IPlaceholderHandler placeholderHandler = new IPlaceholderHandler() {};
                msg = msg.replace("{time_left}", placeholderHandler.handlePlaceholder(player, "time_left"))
                        .replace("{enemy_name}", placeholderHandler.handlePlaceholder(player, "enemy_name"))
                        .replace("{enemy_health}", placeholderHandler.handlePlaceholder(player, "enemy_health"))
                        .replace("{enemy_health_rounded}", placeholderHandler.handlePlaceholder(player, "enemy_health_rounded"))
                        .replace("{enemy_hearts}", placeholderHandler.handlePlaceholder(player, "enemy_hearts"))
                        .replace("{in_combat}", placeholderHandler.handlePlaceholder(player, "in_combat"))
                        .replace("{status}", placeholderHandler.handlePlaceholder(player, "status"));
            }
            
            msg = color(msg)
                    .replace("{bars_left}", getBarsLeft(player))
                    .replace("{bars_right}", getBarsRight(player));
            
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