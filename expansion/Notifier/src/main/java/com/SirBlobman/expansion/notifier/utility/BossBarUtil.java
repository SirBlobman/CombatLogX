package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BossBarUtil extends Util {
    public static void updateBossBar(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt <= 0) {
            removeBossBar(player, false);
        } else {
            LivingEntity enemy = CombatUtil.getEnemy(player);
            String enemyName = (enemy != null) ? ((enemy.getCustomName() != null) ? enemy.getCustomName() : enemy.getName()) : "Unknown";
            String enemyHealth = (enemy != null) ? formatDouble(enemy.getHealth()) : "Unknown";
            String yes = ConfigLang.get("messages.expansions.placeholder compatibility.yes");
            String no = ConfigLang.get("messages.expansions.placeholder compatibility.no");
            String idling = ConfigLang.get("messages.expansions.placeholder compatibility.status.idling");
            String fighting = ConfigLang.get("messages.expansions.placeholder compatibility.status.fighting");
            
            List<String> keys = Util.newList("{time_left}", "{enemy_name}", "{enemy_health}", "{in_combat}", "{status}");
            String timeLeft = (timeLeftInt > 0) ? Integer.toString(timeLeftInt) : ConfigLang.get("messages.expansions.placeholder compatibility.zero time left");
            List<?> vals = Util.newList(timeLeft, enemyName, enemyHealth, CombatUtil.isInCombat(player) ? yes : no, CombatUtil.isInCombat(player) ? fighting : idling);
            String title = formatMessage(ConfigNotifier.BOSS_BAR_FORMAT, keys, vals);

            float fTimeLeft = (float) timeLeftInt;
            float fTotalTime = (float) ConfigOptions.OPTION_TIMER;
            float progress = (fTimeLeft / fTotalTime);
            if (progress <= 0) progress = 0.0F;
            if (progress >= 1) progress = 1.0F;

            LegacyHandler.getLegacyHandler().removeBossBar(player);
            LegacyHandler.getLegacyHandler().sendBossBar(player, ConfigNotifier.BOSS_BAR_STYLE, ConfigNotifier.BOSS_BAR_COLOR, title, progress);
        }
    }

    public static void removeBossBar(Player player, boolean shuttingDown) {
        String title = color(ConfigNotifier.BOSS_BAR_NO_LONGER_IN_COMBAT);
        LegacyHandler.getLegacyHandler().removeBossBar(player);
        LegacyHandler.getLegacyHandler().sendBossBar(player, ConfigNotifier.BOSS_BAR_STYLE, ConfigNotifier.BOSS_BAR_COLOR, title, 0.0F);

        if (shuttingDown) {
            LegacyHandler.getLegacyHandler().removeBossBar(player);
        } else {
            SchedulerUtil.runLaterSync(20L, () -> LegacyHandler.getLegacyHandler().removeBossBar(player));
        }
    }

    private static String formatDouble(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }
}