package com.SirBlobman.expansion.notifier.utility;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.util.List;

import org.bukkit.entity.Player;

public class BossBarUtil extends Util {
    public static void updateBossBar(Player player) {

        int timeLeft = CombatUtil.getTimeLeft(player);
        if (timeLeft <= 0) {
            removeBossBar(player, false);
        } else {
            List<String> keys = newList("{time_left}");
            List<?> vals = newList(timeLeft);
            String title = formatMessage(ConfigNotifier.BOSS_BAR_FORMAT, keys, vals);

            float fTimeLeft = (float) timeLeft;
            float fTotalTime = (float) ConfigOptions.OPTION_TIMER;
            float progress = (fTimeLeft / fTotalTime);
            if (progress <= 0) progress = 0.0F;
            if (progress >= 1) progress = 1.0F;

            LegacyHandler.getLegacyHandler().sendBossBar(player, ConfigNotifier.BOSS_BAR_STYLE, ConfigNotifier.BOSS_BAR_COLOR, title, progress);
        }
    }

    public static void removeBossBar(Player player, boolean shuttingDown) {
        String title = color(ConfigNotifier.BOSS_BAR_NO_LONGER_IN_COMBAT);
        LegacyHandler.getLegacyHandler().sendBossBar(player, ConfigNotifier.BOSS_BAR_STYLE, ConfigNotifier.BOSS_BAR_COLOR, title, 0.0F);

        if (shuttingDown) {
            LegacyHandler.getLegacyHandler().removeBossBar(player);
        } else {
            SchedulerUtil.runLater(20L, () -> LegacyHandler.getLegacyHandler().removeBossBar(player));
        }
    }
}