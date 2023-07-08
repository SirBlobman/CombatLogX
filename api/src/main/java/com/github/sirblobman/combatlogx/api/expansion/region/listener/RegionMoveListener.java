package com.github.sirblobman.combatlogx.api.expansion.region.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class RegionMoveListener extends RegionExpansionListener {
    public RegionMoveListener(@NotNull RegionExpansion expansion) {
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

        RegionHandler<?> regionHandler = getRegionHandler();
        if (regionHandler.isSafeZone(player, toLocation, tagInformation)) {
            Location fromLocation = e.getFrom();
            if (!regionHandler.isSafeZone(player, fromLocation, tagInformation)) {
                regionHandler.preventEntry(e, player, tagInformation, fromLocation, toLocation);
            }
        }
    }
}
