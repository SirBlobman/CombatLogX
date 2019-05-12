package com.SirBlobman.expansion.worldguard.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;
import com.SirBlobman.expansion.worldguard.config.ConfigWG.NoEntryMode;

import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;

public class ListenV6 implements Listener {
    public ListenV6() {
        PluginUtil.regEvents(this);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onWorldGuardDenyPvP(DisallowedPVPEvent e) {
        if(ConfigWG.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
        Player player = e.getDefender();
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(true);
        ListenWorldGuard.sendMessage(player, enemy);
    }
}
