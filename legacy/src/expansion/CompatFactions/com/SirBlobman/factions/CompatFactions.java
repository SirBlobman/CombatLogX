package com.SirBlobman.factions;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.factions.compat.FactionsUtil;
import com.SirBlobman.factions.config.ConfigFactions;

public class CompatFactions implements CLXExpansion, Listener {
    public String getVersion() {
        return "5";
    }
    
    public String getUnlocalizedName() {
        return "CompatFactions";
    }
    
    public String getName() {
        return "Factions Compatibility";
    }
    
    public static File FOLDER;
    private static FactionsUtil FUTIL;
    
    @Override
    public void enable() {
        FUTIL = FactionsUtil.getFactions();
        if (FUTIL == null) {
            String error = "Could not find a valid Factions plugin. Please contact SirBlobman if you think this should not be happening!";
            print(error);
        } else {
            FOLDER = getDataFolder();
            ConfigFactions.load();
            PluginUtil.regEvents(this);
        }
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void onConfigReload() {
        FUTIL = FactionsUtil.getFactions();
        if (FUTIL != null) {
            FOLDER = getDataFolder();
            ConfigFactions.load();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        if (Combat.isInCombat(player)) {
            if (FUTIL.isSafeZone(to)) {
                Location from = e.getFrom();
                preventEntry(e, player, from, to);
            }
        }
    }
    
    public void preventEntry(Cancellable e, Player player, Location from, Location to) {
        LivingEntity enemy = Combat.getEnemy(player);
        if (enemy != null) {
            String noEntryModeString = ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_MODE;
            NoEntryMode nem = NoEntryMode.valueOf(noEntryModeString);
            if (nem == null) nem = NoEntryMode.CANCEL;
            switch (nem) {
            case CANCEL:
                e.setCancelled(true);
                break;
            case TELEPORT:
                player.teleport(enemy);
                break;
            case KNOCKBACK:
                if (!FUTIL.isSafeZone(from)) {
                    Vector v = getVector(from, to);
                    player.setVelocity(v);
                }
                break;
            case KILL:
                player.setHealth(0.0D);
                break;
            }
            
            Util.sendMessage(player, ConfigLang.MESSAGE_NO_ENTRY);
        }
    }
    
    public Vector getVector(Location from, Location to) {
        Vector vfrom = from.toVector();
        Vector vto = to.toVector();
        Vector sub = vfrom.subtract(vto);
        Vector norm = sub.normalize();
        Vector mult = norm.multiply(ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
        return mult.setY(0.0D);
    }
}