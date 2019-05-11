package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.util.List;
import java.util.UUID;

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
        if(DISABLED_PLAYERS.contains(player.getUniqueId())) return;
        
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
        
        NMS_Handler.getHandler().removeBossBar(player);
        NMS_Handler.getHandler().sendBossBar(player, title, progress, ConfigNotifier.BOSS_BAR_COLOR, ConfigNotifier.BOSS_BAR_STYLE);
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