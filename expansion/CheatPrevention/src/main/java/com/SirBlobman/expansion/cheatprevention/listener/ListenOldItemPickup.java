package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class ListenOldItemPickup implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPickupItem(PlayerPickupItemEvent e) {
        if(ConfigCheatPrevention.ITEM_PICK_UP_DURING_COMBAT) return;
        
        Player player = e.getPlayer();
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