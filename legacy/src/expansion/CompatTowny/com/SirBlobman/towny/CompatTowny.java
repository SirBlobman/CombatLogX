package com.SirBlobman.towny;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.towny.config.ConfigTowny;

public class CompatTowny implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if (Util.PM.isPluginEnabled("Towny")) {
            FOLDER = getDataFolder();
            ConfigTowny.load();
            Util.regEvents(this);
        } else {
            String error = "Towny is not installed. This expansion is useless!";
            print(error);
        }
    }
    
    public String getUnlocalizedName() {
        return "CompatTowny";
    }
    
    public String getName() {
        return "Towny Compatibility";
    }
    
    public String getVersion() {
        return "5";
    }
    
    @Override
    public void onConfigReload() {
        ConfigTowny.load();
    }
    
    @EventHandler
    public void onAttack(PlayerTagEvent e) {
        Player tag = e.getPlayer();
        Location tloc = tag.getLocation();
        if (!TownyUtil.pvp(tloc)) e.setCancelled(true);
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();
        if (Combat.isInCombat(p)) {
            if (!TownyUtil.pvp(to)) {
                e.setCancelled(true);
            }
        }
        
        String error = ConfigLang.MESSAGE_NO_ENTRY;
        Util.sendMessage(p, error);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();
        Location from = e.getFrom();
        if (Combat.isInCombat(p)) {
            if (!TownyUtil.pvp(to)) {
                if (ConfigTowny.OPTION_NO_SAFEZONE_ENTRY) {
                    String snem = ConfigTowny.OPTION_NO_SAFEZONE_ENTRY_MODE;
                    NoEntryMode nem = NoEntryMode.valueOf(snem);
                    
                    if (nem == null || nem == NoEntryMode.CANCEL) p.teleport(from);
                    else if (nem == NoEntryMode.KILL) p.setHealth(0.0D);
                    else if (nem == NoEntryMode.TELEPORT) {
                        LivingEntity enemy = Combat.getEnemy(p);
                        if (enemy != null) {
                            Location loc = enemy.getLocation();
                            if (loc == null || !TownyUtil.pvp(loc)) p.teleport(from);
                            else p.teleport(loc);
                        }
                    } else if (nem == NoEntryMode.KNOCKBACK) {
                        Vector vf = from.toVector();
                        Vector vt = to.toVector();
                        Vector sub = vf.subtract(vt);
                        Vector nor = sub.normalize();
                        Vector v = nor.setY(0.0D);
                        p.setVelocity(v);
                    }
                    
                    String error = ConfigLang.MESSAGE_NO_ENTRY;
                    Util.sendMessage(p, error);
                }
            }
        }
    }
}