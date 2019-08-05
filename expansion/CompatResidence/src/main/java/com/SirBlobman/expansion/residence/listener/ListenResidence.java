package com.SirBlobman.expansion.residence.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.residence.CompatResidence;
import com.SirBlobman.expansion.residence.config.ConfigResidence;
import com.SirBlobman.expansion.residence.utility.ResidenceUtil;

public class ListenResidence implements Listener {
	private final CompatResidence expansion;
	public ListenResidence(CompatResidence expansion) {
		this.expansion = expansion;
	}
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onCancelPVP(EntityDamageByEntityEvent e) {
        if(!e.isCancelled()) return;
        if(ConfigResidence.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(!CombatUtil.isInCombat(player)) return;
        if(!CombatUtil.hasEnemy(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        e.setCancelled(false);
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
        
        if(!ResidenceUtil.isSafeZone(toLoc)) return;
        this.expansion.preventEntry(e, player, toLoc, fromLoc);
    }
}