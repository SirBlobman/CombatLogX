package com.github.sirblobman.combatlogx.api.expansion.region.listener;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.expansion.region.configuration.RegionExpansionConfiguration;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class RegionVulnerableListener extends RegionExpansionListener {
    public RegionVulnerableListener(@NotNull RegionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        RegionExpansionConfiguration configuration = getConfiguration();
        NoEntryMode noEntryMode = configuration.getNoEntryMode();
        if (noEntryMode != NoEntryMode.VULNERABLE) {
            return;
        }

        Entity damaged = e.getEntity();
        Player player = getPlayerOrPassenger(damaged);
        if (player == null) {
            return;
        }

        if (!isInCombat(player)) {
            return;
        }

        ICombatManager combatManager = getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        Entity damager = e.getDamager();
        if (!tagInformation.isEnemy(damager)) {
            return;
        }

        Location location = player.getLocation();
        RegionHandler<?> regionHandler = getRegionHandler();
        if (regionHandler.isSafeZone(player, location, tagInformation)) {
            e.setCancelled(false);
        }
    }

    private @Nullable Player getPlayerOrPassenger(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }

        List<Entity> passengerList = getPassengers(entity);
        if (passengerList.isEmpty()) {
            return null;
        }

        for (Entity passenger : passengerList) {
            if (passenger instanceof Player) {
                return (Player) passenger;
            }
        }

        return null;
    }

    private @NotNull List<Entity> getPassengers(@NotNull Entity entity) {
        int minorVersion = VersionUtility.getMinorVersion();
        return (minorVersion < 11 ? getPassengersLegacy(entity) : getPassengersModern(entity));
    }

    private @NotNull List<Entity> getPassengersModern(@NotNull Entity entity) {
        List<Entity> passengerList = entity.getPassengers();
        if (passengerList == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(passengerList);
    }

    @SuppressWarnings("deprecation") // Legacy Method
    private @NotNull List<Entity> getPassengersLegacy(@NotNull Entity entity) {
        Entity passenger = entity.getPassenger();
        if (passenger == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(passenger);
    }
}
