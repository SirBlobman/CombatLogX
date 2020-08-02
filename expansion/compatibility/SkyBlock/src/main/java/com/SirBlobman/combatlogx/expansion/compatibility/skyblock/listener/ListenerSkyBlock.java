package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook.SkyBlockHook;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerSkyBlock implements Listener {
    private final SkyBlockHook hook;
    public ListenerSkyBlock(SkyBlockHook hook) {
        this.hook = hook;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity entity = e.getEnemy();
        if(!(entity instanceof Player)) return;
        
        Player enemy = (Player) entity;
        Player player = e.getPlayer();
        if(!this.hook.doesTeamMatch(player, enemy)) return;
        
        e.setCancelled(true);
    }
}