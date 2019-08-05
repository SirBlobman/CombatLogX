package com.SirBlobman.expansion.lands.listener;

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
import com.SirBlobman.expansion.lands.CompatLands;
import com.SirBlobman.expansion.lands.config.ConfigLands;
import com.SirBlobman.expansion.lands.utility.LandsUtil;

public class ListenLands implements Listener {
	private final CompatLands expansion;
	public ListenLands(CompatLands expansion) {
		this.expansion = expansion;
	}
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onCancelPVP(EntityDamageByEntityEvent e) {
        if(!e.isCancelled()) return;
        if(ConfigLands.getNoEntryMode() != NoEntryMode.VULNERABLE) return;
        
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
        
        if(!LandsUtil.isSafeZone(toLoc)) return;
        this.expansion.preventEntry(e, player, toLoc, fromLoc);
    }
}