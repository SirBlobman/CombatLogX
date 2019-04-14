package com.SirBlobman.expansion.lands;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.lands.config.ConfigLands;
import com.SirBlobman.expansion.lands.config.ConfigLands.NoEntryMode;
import com.SirBlobman.expansion.lands.utility.LandsUtil;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CompatLands implements CLXExpansion, Listener {
    public static File FOLDER;
    private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
    
    public String getUnlocalizedName() {
        return "CompatLands";
    }
    
    public String getName() {
        return "Lands Compatibility";
    }
    
    public String getVersion() {
        return "13.4";
    }
    
    public boolean checkForLands(boolean print) {
        if(!PluginUtil.isEnabled("Lands", "Angeschossen")) {
            if(print) print("Could not find plugin 'Lands'. Automatically disabling...");
            return false;
        }
        
        Plugin landsPlugin = PluginUtil.PM.getPlugin("Lands");
        PluginDescriptionFile landsPDF = landsPlugin.getDescription();
        String version = landsPDF.getVersion();
        
        if(version.startsWith("2.7") || version.startsWith("2.8")) return true;
        
        if(print) print("Only 2.7 or 2.8 are allowed, but you have '" + version + "'! Automatically disabling...");
        return false;
    }
    
    @Override
    public void enable() {
        if(!checkForLands(true)) {
            Expansions.unloadExpansion(this);
            return;
        }
        
        FOLDER = getDataFolder();
        ConfigLands.load();
        PluginUtil.regEvents(this);
    }
    
    @Override
    public void disable() {
        if(checkForLands(false)) LandsUtil.onDisable();
    }
    
    @Override
    public void onConfigReload() {
        if(checkForLands(false)) ConfigLands.load();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEnterLand(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        Location to = e.getTo().clone();
        Location from = e.getFrom().clone();
        if(LandsUtil.isSafeZone(to)) preventEntry(e, player, from, to);
    }
    
    private Vector getVector(Location fromLoc, Location toLoc) {
        Vector fromVector = fromLoc.toVector();
        Vector toVector = toLoc.toVector();
        Vector subtract = fromVector.subtract(toVector);
        Vector normal = subtract.normalize();
        Vector multiply = normal.multiply(ConfigLands.NO_ENTRY_KNOCKBACK_STRENGTH);
        return multiply.setY(0.0D);
    }
    
    private void preventEntry(Cancellable e, Player player, Location fromLoc, Location toLoc) {
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        sendMessage(player);
        
        NoEntryMode nemode = ConfigLands.getNoEntryMode();
        if(nemode == NoEntryMode.CANCEL) {
            e.setCancelled(true);
            return;
        }
        
        if(nemode == NoEntryMode.TELEPORT) {
            player.teleport(enemy);
            return;
        }
        
        if(nemode == NoEntryMode.KNOCKBACK) {
            if(!LandsUtil.isSafeZone(fromLoc)) {
                Vector knockback = getVector(fromLoc, toLoc);
                player.setVelocity(knockback);
            }
            
            return;
        }
    }
    
    private void sendMessage(Player player) {
        if(player == null) return;
        
        UUID uuid = player.getUniqueId();
        if(MESSAGE_COOLDOWN.contains(uuid)) return;
        
        String messageKey = "messages.expansions.lands compatibility.no entry";
        String message = ConfigLang.getWithPrefix(messageKey);
        Util.sendMessage(player, message);
        
        MESSAGE_COOLDOWN.add(uuid);
        SchedulerUtil.runLater(ConfigLands.NO_ENTRY_MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
    }
}