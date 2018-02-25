package com.SirBlobman.worldguard;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.config.NoEntryMode;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.io.File;

public class CompatWorldGuard implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("WorldGuard")) {
            FOLDER = getDataFolder();
            Util.regEvents(this);
        } else {
            String error = "WorldGuard is not installed. This expansion is useless!";
            print(error);
        }
    }

    public String getUnlocalizedName() {return "CompatWorldGuard";}
    public String getName() {return "WorldGuard Compatability";}
    public String getVersion() {return "2.0.0";}

    @EventHandler
    public void pce(PlayerCombatEvent e) {
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        if(ler instanceof Player) { 
            Player p = (Player) ler;
            boolean safe = WorldGuardUtil.isSafeZone(p.getLocation());
            if(safe) e.setCancelled(true);
        }

        if(led instanceof Player) {
            Player p = (Player) led;
            boolean safe = WorldGuardUtil.isSafeZone(p.getLocation());
            if(safe) e.setCancelled(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(e.isCancelled()) return;
        if(ConfigOptions.CHEAT_PREVENT_NO_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p) && WorldGuardUtil.isSafeZone(to)) {
                String mode = ConfigOptions.CHEAT_PREVENT_NO_ENTRY_MODE;
                NoEntryMode nem = NoEntryMode.valueOf(mode);
                if(nem == null) nem = NoEntryMode.CANCEL;
                if(nem == NoEntryMode.CANCEL) e.setCancelled(true);
                else if(nem == NoEntryMode.KILL) p.setHealth(0.0D);
                else if(nem == NoEntryMode.KNOCKBACK) {
                    Vector vector = WorldGuardUtil.getKnockbackVector(from);
                    p.setVelocity(vector);
                }
                
                String error = ConfigLang.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
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
            if(Combat.isInCombat(p) && WorldGuardUtil.isSafeZone(to)) {
                e.setCancelled(true);
                String error = ConfigLang.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
                p.teleport(from);
            }
        }
    }
}