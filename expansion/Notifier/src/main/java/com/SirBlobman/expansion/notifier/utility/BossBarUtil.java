package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
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

            NMS_Handler.getHandler().removeBossBar(player);
            NMS_Handler.getHandler().sendBossBar(player, title, progress, ConfigNotifier.BOSS_BAR_COLOR, ConfigNotifier.BOSS_BAR_STYLE);
        }
    }

    public static void removeBossBar(Player player, boolean shuttingDown) {
        String title = color(ConfigNotifier.BOSS_BAR_NO_LONGER_IN_COMBAT);
        NMS_Handler.getHandler().removeBossBar(player);
        NMS_Handler.getHandler().sendBossBar(player, title, 0.0D, ConfigNotifier.BOSS_BAR_COLOR, ConfigNotifier.BOSS_BAR_STYLE);

        if (shuttingDown) {
            NMS_Handler.getHandler().removeBossBar(player);
        } else {
            SchedulerUtil.runLaterSync(20L, () -> NMS_Handler.getHandler().removeBossBar(player));
        }
    }
}