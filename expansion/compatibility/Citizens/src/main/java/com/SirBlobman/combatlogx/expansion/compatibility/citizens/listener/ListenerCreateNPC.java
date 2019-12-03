package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CitizensCompatibility;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerCreateNPC implements Listener {
    private final CitizensCompatibility expansion;
    public ListenerCreateNPC(CitizensCompatibility expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforePunish(PlayerPunishEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(config.getBoolean("allow-punishments")) return;
        e.setCancelled(true);

        Player player = e.getPlayer();
        LivingEntity enemy = e.getPreviousEnemy();
        NPCManager.createNPC(this.expansion, player, enemy);
    }
}