package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

@SuppressWarnings("deprecation")
public class ListenOldItemPickup implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPickupItem(PlayerPickupItemEvent e) {
        if(ConfigCheatPrevention.ITEM_DROPPING_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.items.dropping not allowed");
        Util.sendMessage(player, error);
    }
}