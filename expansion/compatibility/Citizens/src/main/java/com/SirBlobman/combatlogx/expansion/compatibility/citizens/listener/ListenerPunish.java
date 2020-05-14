package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.NPCManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerPunish implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerPunish(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforePunish(PlayerPunishEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("allow-punishments", false)) e.setCancelled(true);
        
        Player player = e.getPlayer();
        LivingEntity enemy = e.getPreviousEnemy();
        
        NPCManager npcManager = this.expansion.getNPCManager();
        npcManager.createNPC(player, enemy);
    }
}