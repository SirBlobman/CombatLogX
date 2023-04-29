package com.github.sirblobman.combatlogx.api.expansion.region.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.expansion.region.configuration.RegionExpansionConfiguration;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class RegionTeleportListener extends RegionExpansionListener {
    public RegionTeleportListener(@NotNull RegionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        TeleportCause cause = e.getCause();
        RegionExpansionConfiguration configuration = getConfiguration();
        if (configuration.isIgnored(cause)) {
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

        ICombatManager combatManager = getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        RegionHandler<?> regionHandler = getRegionHandler();
        if (regionHandler.isSafeZone(player, toLocation, tagInformation)) {
            e.setCancelled(true);
        }
    }
}
