package com.SirBlobman.expansion.citizens.listener;

import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenCombat implements Listener {
    @EventHandler(ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        if(!ConfigCitizens.getOption("citizens.prevent npc tagging", false)) return;

        LivingEntity enemy = e.getEnemy();
        if(enemy == null) return;

        boolean isNPC = enemy.hasMetadata("NPC");
        if(isNPC) e.setCancelled(true);
    }
}