package com.SirBlobman.expansion.worldguard.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.worldguard.CompatWorldGuard;
import com.SirBlobman.expansion.worldguard.config.ConfigWG;

import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;

public class ListenV6 implements Listener {
	private final CompatWorldGuard expansion;
	public ListenV6(CompatWorldGuard expansion) {
		this.expansion = expansion;
	}
	
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onWorldGuardDenyPvP(DisallowedPVPEvent e) {
        if(ConfigWG.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
        Player player = e.getDefender();
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}
