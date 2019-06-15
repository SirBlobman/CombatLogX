package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;
import java.util.UUID;

public class ListenFlight implements Listener {
    private static final List<UUID> CANCEL_FALL_DAMAGE = Util.newList();
    public static void checkFlight(Player player) {
        if(!player.isFlying() && !player.getAllowFlight()) return;
        
        player.setFlying(false);
        player.setAllowFlight(false);
        if(ConfigCheatPrevention.FLIGHT_PREVENT_FALL_DAMAGE) {
            Util.debug("[Flight Prevention] Added '" + player.getName() + "' to the 'no fall damage' list.");
            CANCEL_FALL_DAMAGE.add(player.getUniqueId());
        }
        
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.disabled");
        Util.sendMessage(player, message);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onFallDamage(EntityDamageEvent e) {
        Util.debug("[Flight Prevention] Checking entity damage event...");
        if(!ConfigCheatPrevention.FLIGHT_PREVENT_FALL_DAMAGE) {
            Util.debug("[Flight Prevention] Fall damage prevention is not enabled, ignoring event.");
            return;
        }
        
        DamageCause cause = e.getCause();
        if(cause != DamageCause.FALL) {
            Util.debug("[Flight Prevention] Damage cause is not FALL, ignoring event.");
            return;
        }
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) {
            Util.debug("[Flight Prevention] Entity is not a Player, ignoring event.");
            return;
        }
        
        Player player = (Player) entity;
        Util.debug("[Flight Prevention] player found, checking fall damage event for '" + player.getName() + "'...");
        if(!CombatUtil.isInCombat(player)) {
            Util.debug("[Flight Prevention] player is not in combat, ignoring event.");
            return;
        }
        
        UUID uuid = player.getUniqueId();
        if(CANCEL_FALL_DAMAGE.contains(uuid)) {
            e.setCancelled(true);
            CANCEL_FALL_DAMAGE.remove(uuid);
            Util.debug("Cancelled event and removed player from 'no fall damage' list. If they still took fall damage, make sure other plugins aren't interfering with EntityDamageEvent");
        } else {
            Util.debug("[Flight Prevention] Player is not in the 'no fall damage' list, ignoring event.");
            return;
        }
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
        if(reason != UntagReason.EXPIRE && reason != UntagReason.EXPIRE_ENEMY_DEATH) {
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