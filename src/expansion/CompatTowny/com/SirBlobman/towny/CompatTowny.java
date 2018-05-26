package com.SirBlobman.towny;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.towny.config.ConfigTowny;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.io.File;

public class CompatTowny implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("Towny")) {
            FOLDER = getDataFolder();
            ConfigTowny.load();
            Util.regEvents(this);
        } else {
            String error = "Towny is not installed. This expansion is useless!";
            print(error);
        }
    }
    
    public String getUnlocalizedName() {return "CompatTowny";}
    public String getName() {return "Towny Compatability";}
    public String getVersion() {return "2";}
    
    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(e.isCancelled()) return;
        if(ConfigTowny.OPTION_NO_SAFEZONE_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p)) {
                if(!TownyUtil.pvp(to)) {
                    String mode = ConfigTowny.OPTION_NO_SAFEZONE_ENTRY_MODE;
                    NoEntryMode nem = NoEntryMode.valueOf(mode);
                    if(nem == null) nem = NoEntryMode.CANCEL;
                    if(nem == NoEntryMode.CANCEL) e.setCancelled(true);
                    else if(nem == NoEntryMode.KILL) p.setHealth(0.0D);
                    else if(nem == NoEntryMode.KNOCKBACK) {
                        Vector vto = to.toVector(); Vector vfrom = from.toVector();
                        Vector vector = vfrom.subtract(vto);
                        vector = vector.normalize();
                        vector = vector.multiply(ConfigTowny.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
                        vector = vector.setY(0);
                        p.setVelocity(vector);
                    } else if(nem == NoEntryMode.TELEPORT) {
                        Entity enemy = Combat.getEnemy(p);
                        if(enemy != null) {
                            Location l = enemy.getLocation();
                            p.teleport(l);
                        }
                    }
                    
                    String error = ConfigLang.MESSAGE_NO_ENTRY;
                    Util.sendMessage(p, error);
                }
            }
        }
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        if(e.isCancelled()) return;
        if(ConfigTowny.OPTION_NO_SAFEZONE_ENTRY && e.getCause() == TeleportCause.ENDER_PEARL) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p)) {
                if(!TownyUtil.pvp(to)) {
                    e.setCancelled(true);
                    String error = ConfigLang.MESSAGE_NO_ENTRY;
                    Util.sendMessage(p, error);
                    p.teleport(from);
                }
            }
        }
    }
}