package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class ListenFlight implements Listener {
    public static void checkFlight(Player player) {
        if(!player.isFlying() && !player.getAllowFlight()) return;
        
        player.setFlying(false);
        player.setAllowFlight(false);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.disabled");
        Util.sendMessage(player, message);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        if(!e.isFlying()) return;
        if(ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.not allowed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        Util.debug("[Flight Re-Enable] Checking flight re-enable for '" + player.getName() + "'.");
        
        UntagReason reason = e.getUntagReason();
        if(reason != UntagReason.EXPIRE) {
            Util.debug("[Flight Re-Enable] Untag Reason is not expire, ignoring player.");
            return;
        }
        
        String perm = ConfigCheatPrevention.FLIGHT_ENABLE_PERMISSION;
        if(perm == null || perm.isEmpty()) {
            Util.debug("[Flight Re-Enable] Config permission is null or empty, ignoring player.");
            return;
        }
        
        Permission permission = new Permission(perm, "Re-enable a player's flight after combat", PermissionDefault.FALSE);
        if(!player.hasPermission(permission)) {
            Util.debug("[Flight Re-Enable] Player does not have permission '" + perm + "'. ignoring player.");
            return;
        }
        
        Util.debug("[Flight Re-Enable] Enabling flight for player.");
        player.setAllowFlight(true);
        player.setFlying(true);
    }
}