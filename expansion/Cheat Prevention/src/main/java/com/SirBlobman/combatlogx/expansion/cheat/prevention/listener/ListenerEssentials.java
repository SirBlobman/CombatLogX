package com.SirBlobman.combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.SirBlobman.combatlogx.expansion.cheat.prevention.CheatPrevention;

import net.ess3.api.IUser;
import net.ess3.api.events.FlyStatusChangeEvent;

public class ListenerEssentials extends CheatPreventionListener {
    public ListenerEssentials(CheatPrevention expansion) {
        super(expansion);
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onToggleFlight(FlyStatusChangeEvent e) {
        if(!e.getValue()) return;
        
        FileConfiguration config = getConfig();
        if (!config.getBoolean("flight.prevent-flying")) return;

        IUser user = e.getAffected();
        Player player = user.getBase();
        if(!isInCombat(player)) return;

        e.setCancelled(true);
        String message = getMessage("cheat-prevention.flight.no-flying");
        sendMessage(player, message);
    }
}