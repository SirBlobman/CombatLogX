package com.SirBlobman.expansion.cheatprevention.listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class ListenElytra implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onToggleElytra(EntityToggleGlideEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player player = (Player) en;
            if (!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS && CombatUtil.isInCombat(player)) {
                if (e.isGliding()) {
                    e.setCancelled(true);
                    player.setGliding(false);
                    
                    String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.not allowed");
                    Util.sendMessage(player, error);
                }
            }
        }
    }
    
    @EventHandler
    public void onTimerChange(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        
        if (!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) {
            if (player.isGliding()) {
                player.setGliding(false);
                String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.disabled");
                Util.sendMessage(player, msg);
            }
        }
    }
}