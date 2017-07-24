package com.SirBlobman.towny;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerMoveEvent;

public class CompatTowny implements CLXExpansion, Listener {
    @Override
    public void enable() {
        if(Util.PM.isPluginEnabled("Towny")) {
            Util.regEvents(this);
        } else {
            String error = "Towny is not installed. This expansion is useless!";
            Util.print(error);
        }
    }
    
    @Override
    public String getName() {return "CompatTowny";}
    
    @Override
    public String getVersion() {return "1.0";}
    
    @EventHandler
    public void move(PlayerMoveEvent e) {
        if(e.isCancelled()) return;
        if(Config.CHEAT_PREVENT_NO_ENTRY) {
            Player p = e.getPlayer();
            Location from = e.getFrom();
            Location to = e.getTo();
            if(Combat.isInCombat(p)) {
                if(!TownyUtil.pvp(to)) {
                    e.setCancelled(true);
                    String error = Config.MESSAGE_NO_ENTRY;
                    Util.sendMessage(p, error);
                    p.teleport(from);
                }
            }
        }
    }
}