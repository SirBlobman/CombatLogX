package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class ListenNewItemPickup implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPickupItem(EntityPickupItemEvent e) {
        if(ConfigCheatPrevention.ITEM_PICK_UP_DURING_COMBAT) return;
        
        Entity entity = e.getEntity();
        if((entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.items.pick up not allowed");
        Util.sendMessage(player, error);
    }
}