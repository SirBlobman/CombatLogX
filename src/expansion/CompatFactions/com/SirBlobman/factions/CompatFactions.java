package com.SirBlobman.factions;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.factions.compat.FactionsUtil;
import com.SirBlobman.factions.config.ConfigFactions;

public class CompatFactions implements CLXExpansion, Listener {
    public static File FOLDER;
    private static FactionsUtil FACTIONS;
    @Override
    public void enable() {
        FACTIONS = FactionsUtil.getFactions();
        if(FACTIONS == null) {
            String error = "A Factions plugin could not be found. This expansion is useless!";
            print(error);
        } else {
            FOLDER = getDataFolder();
            ConfigFactions.load();
            Util.regEvents(this);
        }
    }
    
    public String getUnlocalizedName() {return "CompatFactions";}
    public String getName() {return "Factions Compatability";}
    public String getVersion() {return "3";}
    
    @EventHandler
    public void pce(PlayerCombatEvent e) {
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        
        if(ler instanceof Player) {
            Player p = (Player) ler;
            boolean can = FACTIONS.canAttack(p, led);
            if(!can) e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        checkMoveEvent(p, e, from, to);
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        String cause = e.getCause().name();
        if(cause.equals("CHROUS_FRUIT") || cause.equals("ENDER_PEARL") || cause.equals("PLUGIN")) checkMoveEvent(p, e, from, to);
    }
    
    public static void checkMoveEvent(Player p, Cancellable e, Location from, Location to) {
    	if(e.isCancelled()) return;
        if(ConfigFactions.OPTION_NO_SAFEZONE_ENTRY) {
            if(Combat.isInCombat(p)) {
                Entity enemy = Combat.getEnemy(p);
                if(enemy != null) {
                    if(enemy instanceof Player && FACTIONS.isSafeZone(to)) {
                    	if(p.isInsideVehicle()) p.leaveVehicle();
                        String mode = ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_MODE;
                        NoEntryMode nem = NoEntryMode.valueOf(mode);
                        if(nem == null) nem = NoEntryMode.CANCEL;
                        if(nem == NoEntryMode.CANCEL) e.setCancelled(true);
                        else if(nem == NoEntryMode.KILL) p.setHealth(0.0D);
                        else if(nem == NoEntryMode.KNOCKBACK) {
                            Vector vector = FACTIONS.getSafeZoneKnockbackVector(from, to);
                            p.setVelocity(vector);
                        } else if(nem == NoEntryMode.TELEPORT) {
                            Location l = enemy.getLocation();
                            p.teleport(l);
                        }
                        
                        String error = ConfigLang.MESSAGE_NO_ENTRY;
                        Util.sendMessage(p, error);
                    } else if(FACTIONS.isSafeFromMobs(to)) {
                    	if(p.isInsideVehicle()) p.leaveVehicle();
                        String mode = ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_MODE;
                        NoEntryMode nem = NoEntryMode.valueOf(mode);
                        if(nem == null) nem = NoEntryMode.CANCEL;
                        if(nem == NoEntryMode.CANCEL) e.setCancelled(true);
                        else if(nem == NoEntryMode.KILL) p.setHealth(0.0D);
                        else if(nem == NoEntryMode.KNOCKBACK) {
                            Vector vector = FACTIONS.getMobsZoneKnockbackVector(from, to);
                            p.setVelocity(vector);
                        } else if(nem == NoEntryMode.TELEPORT) {
                            Location l = enemy.getLocation();
                            p.teleport(l);
                        }
                        
                        String error = ConfigLang.MESSAGE_NO_ENTRY;
                        Util.sendMessage(p, error);
                    }
                }
            }
        }
    }
}