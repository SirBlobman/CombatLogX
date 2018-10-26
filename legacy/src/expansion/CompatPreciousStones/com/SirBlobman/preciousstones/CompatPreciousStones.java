package com.SirBlobman.preciousstones;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.preciousstones.config.ConfigPreciousStones;

public class CompatPreciousStones implements CLXExpansion, Listener {
    public static File FOLDER;
    
    @Override
    public void enable() {
        if (PluginUtil.isPluginEnabled("PreciousStones", "Phaed")) {
            FOLDER = getDataFolder();
            ConfigPreciousStones.load();
            Util.regEvents(this);
        } else {
            String error = "PreciousStones is not installed. This expansion is useless!";
            print(error);
        }
    }
    
    public String getUnlocalizedName() {
        return "CompatPreciousStones";
    }
    
    public String getName() {
        return "PreciousStones Compatability";
    }
    
    public String getVersion() {
        return "3";
    }
    
    @Override
    public void onConfigReload() {
        ConfigPreciousStones.load();
    }
    
    @EventHandler
    public void pce(PlayerCombatEvent e) {
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        if (ler instanceof Player) {
            Player p = (Player) ler;
            boolean pvp = StonesUtil.canPvP(p);
            if (!pvp) e.setCancelled(true);
        }
        
        if (led instanceof Player) {
            Player p = (Player) led;
            boolean pvp = StonesUtil.canPvP(p);
            if (!pvp) e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (e.isCancelled()) return;
        if (ConfigPreciousStones.OPTION_NO_SAFEZONE_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if (Combat.isInCombat(p) && !StonesUtil.canPvP(to)) {
                String mode = ConfigPreciousStones.OPTION_NO_SAFEZONE_ENTRY_MODE;
                NoEntryMode nem = NoEntryMode.valueOf(mode);
                if (nem == null) nem = NoEntryMode.CANCEL;
                
                if (nem == NoEntryMode.CANCEL) {
                    e.setCancelled(true);
                } else if (nem == NoEntryMode.KNOCKBACK) {
                    Vector vto = to.toVector();
                    Vector vfrom = from.toVector();
                    Vector vector = vto.subtract(vfrom);
                    vector = vector.normalize();
                    vector = vector.multiply(ConfigPreciousStones.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
                    p.setVelocity(vector);
                } else if (nem == NoEntryMode.KILL) {
                    p.setHealth(0.0D);
                } else if (nem == NoEntryMode.TELEPORT) {
                    Entity enemy = Combat.getEnemy(p);
                    if (enemy != null) {
                        Location l = enemy.getLocation();
                        p.teleport(l);
                    }
                }
                String error = ConfigLang.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
            }
        }
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        if (e.isCancelled()) return;
        if (ConfigPreciousStones.OPTION_NO_SAFEZONE_ENTRY && e.getCause() == TeleportCause.ENDER_PEARL) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if (Combat.isInCombat(p) && !StonesUtil.canPvP(to)) {
                e.setCancelled(true);
                String error = ConfigLang.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
                p.teleport(from);
            }
        }
    }
}