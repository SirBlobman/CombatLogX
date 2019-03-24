package com.SirBlobman.expansion.cheatprevention.listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class ListenElytra implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onToggleElytra(EntityToggleGlideEvent e) {
        if(ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) return;
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(!CombatUtil.isInCombat(player)) return;
        if(!e.isGliding()) return;
        
        e.setCancelled(true);
        player.setGliding(false);
        
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.not allowed");
        Util.sendMessage(player, error);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onTimerChange(PlayerCombatTimerChangeEvent e) {
        if(ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) return;
        
        Player player = e.getPlayer();
        if(!player.isGliding()) return;
        
        player.setGliding(false);
        String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.disabled");
        Util.sendMessage(player, msg);
    }
}