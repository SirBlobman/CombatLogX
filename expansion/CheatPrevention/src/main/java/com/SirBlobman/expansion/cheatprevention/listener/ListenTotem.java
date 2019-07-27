package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class ListenTotem implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onResurrect(EntityResurrectEvent e) {
        if(!ConfigCheatPrevention.ITEM_PREVENT_TOTEMS) return;
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(CombatUtil.isInCombat(player)) e.setCancelled(true);
    }
}