package com.SirBlobman.expansion.worldguard;

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
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

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
        return "13.1";
    }
    
    @Override
    public Boolean preload() { return true; }
    
    @Override
    public void load() {
        FOLDER = getDataFolder();
        if(!PluginUtil.isEnabled("WorldGuard")) {
            print("WorldGuard is not installed, automatically disabling...");
            Expansions.unloadExpansion(this);
            return;
        }
        
        WGUtil.onLoad();
        ConfigWG.load();
    }
    
    @Override
    public void enable() {
        PluginUtil.regEvents(this);
        ConfigWG.checkValidForceField();
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
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (CombatUtil.isInCombat(p)) {
            Location to = e.getTo();
            Location from = e.getFrom();
            
            LivingEntity enemy = CombatUtil.getEnemy(p);
            if(to.getBlock().equals(from.getBlock())) return;
            if (enemy != null) {
                if (enemy instanceof Player) {
                    if (!WGUtil.allowsPvP(to)) preventEntry(e, p, from, to);
                } else {
                    if (!WGUtil.allowsMobCombat(to)) preventEntry(e, p, from, to);
                }
            }
        }
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (CombatUtil.isInCombat(p)) {
            Location to = e.getTo();
            
            LivingEntity enemy = CombatUtil.getEnemy(p);
            if (enemy != null) {
                if (enemy instanceof Player) {
                    if (!WGUtil.allowsPvP(to)) {
                        e.setCancelled(true);
                        String msg = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.pvp");
                        p.sendMessage(msg);
                    }
                } else {
                    if (!WGUtil.allowsMobCombat(to)) {
                        e.setCancelled(true);
                        String msg = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.mob");
                        p.sendMessage(msg);
                    }
                }
            }
        }
    }
    
    private void preventEntry(Cancellable e, Player p, Location from, Location to) {
        if (CombatUtil.hasEnemy(p)) {
            LivingEntity enemy = CombatUtil.getEnemy(p);
            
            NoEntryMode nem = ConfigWG.getNoEntryMode();
            switch (nem) {
            case CANCEL:
                e.setCancelled(true);
                break;
            case TELEPORT:
                p.teleport(enemy);
                break;
            case KNOCKBACK:
                if ((enemy instanceof Player && WGUtil.allowsPvP(from)) || (!(enemy instanceof Player) && WGUtil.allowsMobCombat(from))) {
                    Vector v = getVector(from, to);
                    p.setVelocity(v);
                }
                break;
            case KILL:
                p.setHealth(0.0D);
                break;
            }
            
            UUID uuid = p.getUniqueId();
            if (!MESSAGE_COOLDOWN.contains(uuid)) {
                if (enemy instanceof Player) {
                    String msg = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.pvp");
                    p.sendMessage(msg);
                } else {
                    String msg = ConfigLang.getWithPrefix("messages.expansions.worldguard compatibility.no entry.mob");
                    p.sendMessage(msg);
                }
                
                MESSAGE_COOLDOWN.add(uuid);
                SchedulerUtil.runLater(ConfigWG.MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
            }
        }
    }
    
    private Vector getVector(Location from, Location to) {
        Vector vfrom = from.toVector();
        Vector vto = to.toVector();
        Vector sub = vfrom.subtract(vto);
        Vector norm = sub.normalize();
        Vector mult = norm.multiply(ConfigWG.NO_ENTRY_KNOCKBACK_STRENGTH);
        return mult.setY(0.0D);
    }
}