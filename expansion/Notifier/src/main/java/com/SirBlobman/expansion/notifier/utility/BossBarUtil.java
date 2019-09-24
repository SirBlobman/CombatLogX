package com.SirBlobman.expansion.notifier.utility;

import java.util.List;
import java.util.UUID;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.notifier.hook.PlaceholderHandler;

import org.bukkit.entity.Player;

public class BossBarUtil extends Util {
    private static final List<UUID> DISABLED_PLAYERS = Util.newList();
    
    /**
     * Toggle if the action bar is disabled or not
     * @param player the player to toggle
     * @return {@code true} if enabled, {@code false} if disabled.
     */
    public static boolean toggle(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(DISABLED_PLAYERS.contains(uuid)) {
            DISABLED_PLAYERS.remove(uuid);
            updateBossBar(player);
        } else {
            DISABLED_PLAYERS.add(uuid);
            removeBossBar(player, false);
        }
        
        return !DISABLED_PLAYERS.contains(uuid);
    }
    
    public static void updateBossBar(Player player) {
        UUID uuid = player.getUniqueId();
        if(DISABLED_PLAYERS.contains(uuid)) return;
        
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if (timeLeftInt <= 0) {
            removeBossBar(player, false);
            return;
        }
        
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

        forceSendBossBar(player, title, progress);
    }
    
    public static void removeBossBar(Player player, boolean shuttingDown) {
        UUID uuid = player.getUniqueId();
        if(DISABLED_PLAYERS.contains(uuid)) return;

        if(shuttingDown) {
            forceRemoveBossBar(player);
            return;
        }

        String title = color(ConfigNotifier.BOSS_BAR_NO_LONGER_IN_COMBAT);
        forceSendBossBar(player, title, 0.0D);
        SchedulerUtil.runLaterSync(20L, () -> forceRemoveBossBar(player));
    }

    private static void forceRemoveBossBar(Player player) {
        try {
            NMS_Handler nmsHandler = NMS_Handler.getHandler();
            nmsHandler.removeBossBar(player);
        } catch(Exception ex) {
            Util.print("An error occurred while removing a boss bar.");
            ex.printStackTrace();
        }
    }

    private static void forceSendBossBar(Player player, String title, double progress) {
        try {
            NMS_Handler nmsHandler = NMS_Handler.getHandler();
            nmsHandler.sendNewBossBar(player, title, progress, ConfigNotifier.BOSS_BAR_COLOR, ConfigNotifier.BOSS_BAR_STYLE);
        } catch(Exception ex) {
            Util.print("An error occurred while sending a boss bar.");
            ex.printStackTrace();
        }
    }
}