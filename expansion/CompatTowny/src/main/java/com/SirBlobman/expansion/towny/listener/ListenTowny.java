package com.SirBlobman.expansion.towny.listener;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.towny.CompatTowny;
import com.SirBlobman.expansion.towny.config.ConfigTowny;
import com.SirBlobman.expansion.towny.utility.TownyUtil;

import com.palmergames.bukkit.towny.event.DisallowedPVPEvent;

public class ListenTowny implements Listener {
	private final CompatTowny expansion;
	public ListenTowny(CompatTowny expansion) {
		this.expansion = expansion;
	}
	
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCancelPVP(DisallowedPVPEvent e) {
        if(ConfigTowny.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
        Player player = e.getDefender();
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        
        if(!TownyUtil.isSafeZone(toLoc)) return;
        this.expansion.preventEntry(e, player, toLoc, fromLoc);
    }
}
