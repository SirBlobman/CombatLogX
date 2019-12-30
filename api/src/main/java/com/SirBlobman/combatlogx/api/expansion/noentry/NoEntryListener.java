package com.SirBlobman.combatlogx.api.expansion.noentry;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

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

public class NoEntryListener implements Listener {
    private final NoEntryExpansion expansion;
    public NoEntryListener(NoEntryExpansion expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCancelPVP(EntityDamageByEntityEvent e) {
        if(!e.isCancelled()) return;
        if(this.expansion.getNoEntryHandler().getNoEntryMode() != NoEntryMode.VULNERABLE) return;

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
        TagType tagType = (enemy instanceof Player ? TagType.PLAYER : TagType.MOB);

        NoEntryHandler handler = this.expansion.getNoEntryHandler();
        if(!handler.isSafeZone(player, toLocation, tagType)) return;

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
        TagType tagType = (enemy instanceof Player ? TagType.PLAYER : TagType.MOB);

        NoEntryHandler handler = this.expansion.getNoEntryHandler();
        if(!handler.isSafeZone(player, toLocation, tagType)) return;

        e.setCancelled(true);
        this.expansion.sendNoEntryMessage(player, enemy);
    }
}