package com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.preciousstones.CompatibilityPreciousStones;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.sacredlabyrinth.Phaed.PreciousStones.api.events.FieldPreCreationEvent;

public class ListenerFieldCreation implements Listener {
    private final CompatibilityPreciousStones expansion;
    public ListenerFieldCreation(CompatibilityPreciousStones expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeFieldCreation(FieldPreCreationEvent e) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
    
        Player player = e.getPlayer();
        if(!combatManager.isInCombat(player)) return;
    
        FileConfiguration config = this.expansion.getConfig("preciousstones-compatibility.yml");
        if(!config.getBoolean("prevent-field-creation", true)) return;
        e.setCancelled(true);
        
        String message = plugin.getLanguageMessageColoredWithPrefix("preciousstones-compatibility-no-field");
        plugin.sendMessage(player, message);
    }
}