package com.SirBlobman.combatlogx.expansion.compatibility.factions.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.NoEntryExpansion.NoEntryMode;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.CompatibilityFactions;
import com.SirBlobman.combatlogx.expansion.compatibility.factions.hook.FactionsHook;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ListenerFactions implements Listener {
    private final CompatibilityFactions expansion;
    private final FactionsHook factionsHook;
    public ListenerFactions(CompatibilityFactions expansion, FactionsHook factionsHook) {
        this.expansion = expansion;
        this.factionsHook = factionsHook;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCancelPVP(EntityDamageByEntityEvent e) {
        if(!e.isCancelled()) return;
        if(this.expansion.getNoEntryMode() != NoEntryMode.VULNERABLE) return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager manager = plugin.getCombatManager();

        Player player = (Player) entity;
        if(!manager.isInCombat(player)) return;

        LivingEntity enemy = manager.getEnemy(player);
        if(enemy == null) return;

        e.setCancelled(false);
        this.expansion.sendNoEntryMessage(player, enemy);
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
        if(!this.factionsHook.isSafeZone(toLocation)) return;

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
        if(!this.factionsHook.isSafeZone(toLocation)) return;

        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}