package com.SirBlobman.expansion.worldguard;

import org.bukkit.Location;
import org.bukkit.block.Block;
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
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.config.ConfigWG.NoEntryMode;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CompatWorldGuard implements CLXExpansion, Listener {
    public static File FOLDER;
    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
    
    public String getUnlocalizedName() {
        return "CompatWorldGuard";
    }
    
    public String getName() {
        return "WorldGuard Compatibility";
    }
    
    public String getVersion() {
        return "13.2";
    }
    
    @Override
    public Boolean preload() { return true; }
    
    @Override
    public void load() {
        FOLDER = getDataFolder();
        if(Util.PM.getPlugin("WorldGuard") == null) {
            print("WorldGuard is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        WGUtil.onLoad();
        ConfigWG.load();
    }
    
    @Override
    public void enable() {
        if(PluginUtil.isEnabled("WorldGuard")) {
            PluginUtil.regEvents(this);
            ConfigWG.checkValidForceField();
        }
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        if (PluginUtil.isEnabled("WorldGuard")) {
            ConfigWG.load();
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true) 
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        
        Block toBlock = toLoc.getBlock();
        Block fromBlock = fromLoc.getBlock();
        if(toBlock.equals(fromBlock)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
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
        Vector fromVector = fromLoc.toVector();
        Vector toVector = toLoc.toVector();
        Vector subtract = fromVector.subtract(toVector);
        Vector normal = subtract.normalize();
        Vector multiply = normal.multiply(ConfigWG.NO_ENTRY_KNOCKBACK_STRENGTH);
        return multiply.setY(0.0D);
    }
    
    private void preventEntry(Cancellable e, Player player, Location fromLoc, Location toLoc) {
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        sendMessage(player, enemy);
        
        NoEntryMode nemode = ConfigWG.getNoEntryMode();
        if(nemode == NoEntryMode.CANCEL) {
            e.setCancelled(true);
            return;
        }
        
        if(nemode == NoEntryMode.TELEPORT) {
            player.teleport(enemy);
            return;
        }
        
        if(nemode == NoEntryMode.KNOCKBACK) {
            boolean isPVP = (enemy instanceof Player);
            if(isPVP && !WGUtil.allowsPvP(fromLoc)) return;
            if(!isPVP && !WGUtil.allowsMobCombat(fromLoc)) return;
            
            Vector knockback = getVector(fromLoc, toLoc);
            player.setVelocity(knockback);
            return;
        }
    }
    
    private void sendMessage(Player player, LivingEntity enemy) {
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