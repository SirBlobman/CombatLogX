package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerNewItemPickup extends CheatPreventionListener {
    public ListenerNewItemPickup(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPickupItem(EntityPickupItemEvent e) {
        LivingEntity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        FileConfiguration config = getConfig();
        if(!config.getBoolean("items.prevent-item-pickup")) return;

        Player player = (Player) entity;
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        sendMessageWithCooldown(player, "cheat-prevention.items.no-pickup");
    }
}