package com.SirBlobman.expansion.worldguard.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.config.ConfigWG.NoEntryMode;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;

public class ListenWorldGuard implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(WGUtil.allowsTagging(location)) return;
        
        e.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true) 
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        
        if(enemy instanceof Player && WGUtil.allowsPvP(toLoc)) return;
        if(!(enemy instanceof Player) && WGUtil.allowsMobCombat(toLoc)) return;
        
        preventEntry(e, player, fromLoc, toLoc);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        if(enemy instanceof Player) {
            if(WGUtil.allowsPvP(toLoc)) return;
            e.setCancelled(true);
            String noEntryMessage = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.pvp");
            Util.sendMessage(player, noEntryMessage);
            return;
        }
        
        if(WGUtil.allowsMobCombat(toLoc)) return;
        e.setCancelled(true);
        String noEntryMessage = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.mob");
        Util.sendMessage(player, noEntryMessage);
    }
    
    private Vector getVector(Location fromLoc, Location toLoc) {
    	Vector normal = Util.getVector(fromLoc, toLoc);
    	Vector multiply = normal.multiply(ConfigWG.NO_ENTRY_KNOCKBACK_STRENGTH);
    	return Util.makeFinite(multiply);
    }
    
    private void preventEntry(Cancellable e, Player player, Location fromLoc, Location toLoc) {
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        sendMessage(player, enemy);
        
        NoEntryMode nemode = ConfigWG.getNoEntryMode();
        if(nemode == NoEntryMode.VULNERABLE) return;
        
        if(nemode == NoEntryMode.CANCEL) {
            e.setCancelled(true);
            return;
        }
        
        if(nemode == NoEntryMode.TELEPORT) {
            player.teleport(enemy);
            return;
        }
        
        if(nemode == NoEntryMode.KNOCKBACK) {
            e.setCancelled(true);
            SchedulerUtil.runLater(1L, () -> {
                Vector knockback = getVector(fromLoc, toLoc);
                player.setVelocity(knockback);
            });
            return;
        }
    }
    
    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
    public static void sendMessage(Player player, LivingEntity enemy) {
        if(player == null || enemy == null) return;
        
        UUID uuid = player.getUniqueId();
        if(MESSAGE_COOLDOWN.contains(uuid)) return;
        
        String messageKey = "messages.expansions.worldguard compatibility.no entry." + (enemy instanceof Player ? "pvp" : "mob");
        String message = ConfigLang.getWithPrefix(messageKey);
        Util.sendMessage(player, message);
        
        MESSAGE_COOLDOWN.add(uuid);
        SchedulerUtil.runLater(ConfigWG.MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
    }
}