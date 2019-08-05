package com.SirBlobman.expansion.worldguard.listener;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.expansion.worldguard.CompatWorldGuard;
import com.SirBlobman.expansion.worldguard.utility.WGUtil;

public class ListenWorldGuard implements Listener {
	private final CompatWorldGuard expansion;
	public ListenWorldGuard(CompatWorldGuard expansion) {
		this.expansion = expansion;
	}
	
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(WGUtil.allowsTagging(location)) return;
        
        e.setCancelled(true);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true) 
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        
        if(enemy instanceof Player && WGUtil.allowsPvP(toLoc)) return;
        if(!(enemy instanceof Player) && WGUtil.allowsMobCombat(toLoc)) return;
        
        this.expansion.preventEntry(e, player, toLoc, fromLoc);
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        
        LivingEntity enemy = CombatUtil.getEnemy(player);
        if(enemy == null) return;
        
        Location toLoc = e.getTo();
        if(enemy instanceof Player) {
            if(WGUtil.allowsPvP(toLoc)) return;
            e.setCancelled(true);
            
            this.expansion.sendNoEntryMessage(player, enemy);
            return;
        }
        
        if(WGUtil.allowsMobCombat(toLoc)) return;
        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}