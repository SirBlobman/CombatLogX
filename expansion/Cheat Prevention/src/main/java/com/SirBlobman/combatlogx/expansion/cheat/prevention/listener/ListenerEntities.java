package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ListenerEntities implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerEntities(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onInteract(PlayerInteractEntityEvent e) {
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if(!config.getBoolean("prevent-entity-interaction")) return;

        Player player = e.getPlayer();
        ICombatManager manager = this.plugin.getCombatManager();
        if(!manager.isInCombat(player)) return;

        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.no-entity-interaction");
        this.plugin.sendMessage(player, message);
    }
}