package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

public class ListenerEntityInteraction extends CheatPreventionListener {
    public ListenerEntityInteraction(CheatPrevention expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onInteract(PlayerInteractEntityEvent e) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("prevent-entity-interaction")) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.no-entity-interaction");
        sendMessage(player, message);
    }
}