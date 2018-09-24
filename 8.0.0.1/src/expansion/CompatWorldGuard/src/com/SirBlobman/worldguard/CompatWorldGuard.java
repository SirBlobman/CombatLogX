package com.SirBlobman.worldguard;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.worldguard.config.ConfigWorldGuard;
import com.SirBlobman.worldguard.olivolja3.ForceField;

public class CompatWorldGuard implements CLXExpansion, Listener {
    public static File FOLDER;

    @Override
    public void enable() {
        if (Util.PM.isPluginEnabled("WorldGuard")) {
            FOLDER = getDataFolder();
            ConfigWorldGuard.load();
            Util.regEvents(this);
            if(ConfigWorldGuard.OPTION_FORCEFIELD_ENABLED) Util.regEvents(new ForceField());
        } else {
            String error = "WorldGuard is not installed. This expansion is useless!";
            print(error);
        }
    }

    public String getUnlocalizedName() {return "CompatWorldGuard";}
    public String getName() {return "WorldGuard Compatability";}
    public String getVersion() {return "7";}
    
    @Override
    public void onConfigReload() {
        ConfigWorldGuard.load();
    }

    @EventHandler
    public void pce(PlayerCombatEvent e) {
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        if (ler instanceof Player) {
            Player p = (Player) ler;
            boolean safe = WorldGuardUtil.isSafeZone(p.getLocation());
            if (safe)
                e.setCancelled(true);
        }

        if (led instanceof Player) {
            Player p = (Player) led;
            boolean safe = WorldGuardUtil.isSafeZone(p.getLocation());
            if (safe)
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        World world = to.getWorld();
        String wname = world.getName().toLowerCase();
        if(!ConfigWorldGuard.OPTION_DISABLED_WORLDS.contains(wname)) checkEvent(p, e, from, to);
    }

    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        World world = to.getWorld();
        String wname = world.getName().toLowerCase();
        if(!ConfigWorldGuard.OPTION_DISABLED_WORLDS.contains(wname)) {
            String cause = e.getCause().name();
            if (cause.equals("CHROUS_FRUIT") || cause.equals("ENDER_PEARL") || cause.equals("PLUGIN")) checkEvent(p, e, from, to);
        }
    }

    public static void checkEvent(Player p, Cancellable e, Location from, Location to) {
        if (ConfigWorldGuard.OPTION_NO_SAFEZONE_ENTRY) {
            if (Combat.isInCombat(p)) {
                Entity enemy = Combat.getEnemy(p);
                if (enemy != null) {
                    if (enemy instanceof Player) {
                        if(WorldGuardUtil.isSafeZone(to)) {
                            if (p.isInsideVehicle()) p.leaveVehicle();
                            String mode = ConfigWorldGuard.OPTION_NO_SAFEZONE_ENTRY_MODE;
                            NoEntryMode nem = NoEntryMode.valueOf(mode);
                            if (nem == null)  nem = NoEntryMode.CANCEL;
                            
                            if (nem == NoEntryMode.CANCEL) e.setCancelled(true);
                            else if (nem == NoEntryMode.KILL) p.setHealth(0.0D);
                            else if (nem == NoEntryMode.KNOCKBACK) {
                                Vector vector = from.toVector().subtract(to.toVector());
                                vector = vector.normalize();
                                vector = vector.setY(0.0D);
                                vector = vector.multiply(ConfigWorldGuard.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
                                p.setVelocity(vector);
                            } else if (nem == NoEntryMode.TELEPORT) {
                                Location l = enemy.getLocation();
                                p.teleport(l);
                            }

                            String error = ConfigLang.MESSAGE_NO_ENTRY;
                            Util.sendMessage(p, error);
                        }
                    } else if (WorldGuardUtil.isSafeFromMobs(to)) {
                        if (p.isInsideVehicle())
                            p.leaveVehicle();
                        String mode = ConfigWorldGuard.OPTION_NO_SAFEZONE_ENTRY_MODE;
                        NoEntryMode nem = NoEntryMode.valueOf(mode);
                        if (nem == null)
                            nem = NoEntryMode.CANCEL;
                        if (nem == NoEntryMode.CANCEL)
                            e.setCancelled(true);
                        else if (nem == NoEntryMode.KILL)
                            p.setHealth(0.0D);
                        else if (nem == NoEntryMode.KNOCKBACK) {
                            Vector vector = from.toVector().subtract(to.toVector());
                            vector = vector.normalize();
                            vector = vector.setY(0.0D);
                            vector = vector.multiply(ConfigWorldGuard.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
                            p.setVelocity(vector);
                        } else if (nem == NoEntryMode.TELEPORT) {
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