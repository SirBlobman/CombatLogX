package com.SirBlobman.combatlogx.expansion.compatibility.worldguard.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.CompatibilityWorldGuard;
import com.SirBlobman.combatlogx.expansion.compatibility.worldguard.hook.HookWorldGuard;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ListenerWorldGuard implements Listener {
    private final CompatibilityWorldGuard expansion;
    public ListenerWorldGuard(CompatibilityWorldGuard expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        if(HookWorldGuard.allowsTagging(location)) return;

        e.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        Player player = e.getPlayer();
        if(!manager.isInCombat(player)) return;

        LivingEntity enemy = manager.getEnemy(player);
        if(enemy == null) return;

        Location toLocation = e.getTo();
        if(enemy instanceof Player && HookWorldGuard.allowsPVP(toLocation)) return;
        if(!(enemy instanceof Player) && HookWorldGuard.allowsMobCombat(toLocation)) return;

        Location fromLocation = e.getFrom();
        this.expansion.preventEntry(e, player, fromLocation, toLocation);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent e) {
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        Player player = e.getPlayer();
        if(!manager.isInCombat(player)) return;

        LivingEntity enemy = manager.getEnemy(player);
        if(enemy == null) return;

        Location toLocation = e.getTo();
        if(enemy instanceof Player && HookWorldGuard.allowsPVP(toLocation)) return;
        if(!(enemy instanceof Player) && HookWorldGuard.allowsMobCombat(toLocation)) return;

        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}