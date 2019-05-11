package com.SirBlobman.expansion.notifier.utility;

import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;

import java.util.List;
import java.util.UUID;

public class ActionBarUtil extends Util {
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
            updateActionBar(player);
        } else {
            DISABLED_PLAYERS.add(uuid);
            removeActionBar(player);
        }
        
        return !DISABLED_PLAYERS.contains(uuid);
    }
    
    public static void updateActionBar(Player player) {
        if(DISABLED_PLAYERS.contains(player.getUniqueId())) return;
        
        int timeLeftInt = CombatUtil.getTimeLeft(player);
        if(timeLeftInt <= 0) {
            removeActionBar(player);
            return;
        }
        
        String msg = ConfigNotifier.ACTION_BAR_FORMAT;
        if(Expansions.isEnabled("CompatPlaceholders")) {
            PlaceholderHandler handler = new PlaceholderHandler();
            msg = handler.replaceAllPlaceholders(player, msg);
        }
        
        msg = color(msg).replace("{bars_left}", getBarsLeft(player)).replace("{bars_right}", getBarsRight(player));
        
        NMS_Handler.getHandler().sendActionBar(player, msg);
    }
    
    public static void removeActionBar(Player player) {
        String msg = color(ConfigNotifier.ACTION_BAR_NO_LONGER_IN_COMBAT);
        NMS_Handler.getHandler().sendActionBar(player, msg);
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