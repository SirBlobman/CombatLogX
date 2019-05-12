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
        Util.debug("[Anti-Elytra] EntityToggleGlideEvent triggered.");
        
        if(ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) {
            Util.debug("[Anti-Elytra] Config set to allow elytras, ignoring event.");
            return;
        }
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) {
            Util.debug("[Anti-Elytra] Entity gliding is not a player, ignoring event.");
            return;
        }
        
        Player player = (Player) entity;
        Util.debug("[Anti-Elytra] Found player named '" + player.getName() + ". Checking event...");
        
        if(!CombatUtil.isInCombat(player)) {
            Util.debug("[Anti-Elytra] Player is not in combat, ignoring event.");
            return;
        }
        
        if(!e.isGliding()) {
            Util.debug("[Anti-Elytra] Event will not enable gliding, ignoring event.");
            return;
        }
        
        Util.debug("[Anti-Elytra] Cancelling EntityToggleGlideEvent for player '" + player.getName() + ".");
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