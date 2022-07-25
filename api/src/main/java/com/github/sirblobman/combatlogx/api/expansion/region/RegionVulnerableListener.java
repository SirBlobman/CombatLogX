package com.github.sirblobman.combatlogx.api.expansion.region;

import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

public final class RegionVulnerableListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;

    public RegionVulnerableListener(RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        RegionHandler regionHandler = this.regionExpansion.getRegionHandler();
        NoEntryMode noEntryMode = regionHandler.getNoEntryMode();
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

        Location location = player.getLocation();
        if (regionHandler.isSafeZone(player, location, tagInformation)) {
            e.setCancelled(false);
        }
    }

    private Player getPlayerOrPassenger(Entity entity) {
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

    @SuppressWarnings("deprecation")
    private List<Entity> getPassengers(Entity entity) {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 11) {
            Entity passenger = entity.getPassenger();
            if (passenger == null) {
                return Collections.emptyList();
            }

            return Collections.singletonList(passenger);
        }

        return entity.getPassengers();
    }
}
