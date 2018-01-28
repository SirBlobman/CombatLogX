package com.SirBlobman.towny;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class CompatTowny implements CLXExpansion, Listener {
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("Towny")) {
            Util.regEvents(this);
        } else {
            String error = "Towny is not installed. This expansion is useless!";
            print(error);
        }
    }
    
    @Override
    public String getName() {return "Towny Compatability";}
    
    @Override
    public String getVersion() {return "1.0";}
    
    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(e.isCancelled()) return;
        if(ConfigOptions.CHEAT_PREVENT_NO_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p)) {
                if(!TownyUtil.pvp(to)) {
                    String mode = ConfigOptions.CHEAT_PREVENT_NO_ENTRY_MODE;
                    NoEntryMode nem = NoEntryMode.valueOf(mode);
                    if(nem == null) nem = NoEntryMode.CANCEL;
                    
                    if(nem == NoEntryMode.CANCEL) {
                        e.setCancelled(true);
                    } else if(nem == NoEntryMode.KNOCKBACK) {
                        Vector vto = to.toVector(); Vector vfrom = from.toVector();
                        Vector vector = vto.subtract(vfrom);
                        vector = vector.multiply(-1 * ConfigOptions.CHEAT_PREVENT_NO_ENTRY_STRENGTH);
                        p.setVelocity(vector);
                    } else if(nem == NoEntryMode.KILL) {
                        p.setHealth(0.0D);
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
        if(ConfigOptions.CHEAT_PREVENT_NO_ENTRY && e.getCause() == TeleportCause.ENDER_PEARL) {
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