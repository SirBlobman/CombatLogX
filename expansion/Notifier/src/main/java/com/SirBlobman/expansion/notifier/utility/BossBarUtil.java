package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.combatlogx.utility.legacy.LegacyHandler;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

public class BossBarUtil extends Util {
    public static void updateBossBar(Player player) {
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt <= 0) {
            removeBossBar(player, false);
        } else {
            String title = ConfigNotifier.BOSS_BAR_FORMAT;
            if(Expansions.isEnabled("CompatPlaceholders")) {
                PlaceholderHandler handler = new PlaceholderHandler();
                title = handler.replaceAllPlaceholders(player, title);
            }
            
            title = color(title);

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
}