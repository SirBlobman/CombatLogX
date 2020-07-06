package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerCombat implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerCombat(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(config.getBoolean("npc-tagging", false)) return;
        
        LivingEntity enemy = e.getEnemy();
        if(enemy == null || !enemy.hasMetadata("NPC")) return;
        
        e.setCancelled(true);
    }
}