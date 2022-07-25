package com.github.sirblobman.combatlogx.api.expansion.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class RegionMoveListener extends RegionListener {
    public RegionMoveListener(RegionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location toLocation = e.getTo();
        if (toLocation == null) {
            return;
        }

        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        RegionHandler regionHandler = getRegionHandler();
        if (regionHandler.isSafeZone(player, toLocation, tagInformation)) {
            Location fromLocation = e.getFrom();
            if (!regionHandler.isSafeZone(player, fromLocation, tagInformation)) {
                regionHandler.preventEntry(e, player, tagInformation, fromLocation, toLocation);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        TeleportCause teleportCause = e.getCause();
        if (teleportCause != TeleportCause.ENDER_PEARL) {
            return;
        }

        Location toLocation = e.getTo();
        if (toLocation == null) {
            return;
        }

        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        RegionHandler regionHandler = getRegionHandler();
        if (regionHandler.isSafeZone(player, toLocation, tagInformation)) {
            e.setCancelled(true);
        }
    }
}
