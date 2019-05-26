package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;
import java.util.UUID;

public class ListenNewItemPickup implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPickupItem(EntityPickupItemEvent e) {
        if(ConfigCheatPrevention.ITEM_PICK_UP_DURING_COMBAT) return;
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        sendMessage(player);
    }

    private final List<UUID> MESSAGE_COOLDOWN = Util.newList();
    private void sendMessage(Player player) {
        UUID uuid = player.getUniqueId();
        if(!MESSAGE_COOLDOWN.contains(uuid)) {
            String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.items.pick up not allowed");
            Util.sendMessage(player, error);
            
            MESSAGE_COOLDOWN.add(uuid);
            SchedulerUtil.runLater(20L * 10L, () -> MESSAGE_COOLDOWN.remove(uuid));
        }
    }
}