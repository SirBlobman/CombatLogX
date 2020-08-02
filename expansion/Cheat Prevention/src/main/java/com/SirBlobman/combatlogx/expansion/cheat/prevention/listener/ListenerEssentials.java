package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.ess3.api.IUser;
import net.ess3.api.events.FlyStatusChangeEvent;

public class ListenerEssentials implements Listener {
    private final Expansion expansion;
    private final ICombatLogX plugin;
    public ListenerEssentials(Expansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onToggleFlight(FlyStatusChangeEvent e) {
        if(!e.getValue()) return;
        
        FileConfiguration config = this.expansion.getConfig("cheat-prevention.yml");
        if (!config.getBoolean("flight.prevent-flying")) return;
        
        IUser affectedUser = e.getAffected();
        Player player = affectedUser.getBase();
        ICombatManager manager = this.plugin.getCombatManager();
        if (!manager.isInCombat(player)) return;
        
        e.setCancelled(true);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("cheat-prevention.flight.no-flying");
        this.plugin.sendMessage(player, message);
    }
}