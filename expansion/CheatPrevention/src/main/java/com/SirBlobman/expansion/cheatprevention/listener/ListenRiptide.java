package com.SirBlobman.expansion.cheatprevention.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class ListenRiptide implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        if(ConfigCheatPrevention.FLIGHT_ALLOW_RIPTIDE) return;
        
        Player player = e.getPlayer();
        if(!player.isRiptiding()) return;
        if(!CombatUtil.isInCombat(player)) return;
        
        e.setCancelled(true);
        String message = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.riptide not allowed");
        ListenCheatPrevention.sendMessage(player, message);
    }
}
