package com.SirBlobman.factions;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.factions.compat.FactionsUtil;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CompatFactions implements CLXExpansion, Listener {
    private static FactionsUtil FACTIONS;
    @Override
    public void enable() {
        FACTIONS = FactionsUtil.getFactions();
        if(FACTIONS == null) {
            String error = "A Factions plugin could not be found. This expansion is useless!";
            Util.print(error);
        } else {
            Util.regEvents(this);
        }
    }
    
    @Override
    public String getName() {return "Factions Compatability";}
    
    @Override
    public String getVersion() {return "1.0.0";}
    
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
        if(e.isCancelled()) return;
        if(Config.CHEAT_PREVENT_NO_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p) && FACTIONS.isSafeZone(to)) {
                e.setCancelled(true);
                String error = Config.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
                p.teleport(from);
            }
        }
    }
    
    @EventHandler
    public void tp(PlayerTeleportEvent e) {
        if(e.isCancelled()) return;
        if(Config.CHEAT_PREVENT_NO_ENTRY && e.getCause() == TeleportCause.ENDER_PEARL) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p) && FACTIONS.isSafeZone(to)) {
                e.setCancelled(true);
                String error = Config.MESSAGE_NO_ENTRY;
                Util.sendMessage(p, error);
                p.teleport(from);
            }
        }
    }
}