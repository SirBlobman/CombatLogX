package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ListenerWorldGuard implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(HookWorldGuard.allowsTagging(location)) return;

        e.setCancelled(true);
    }
}