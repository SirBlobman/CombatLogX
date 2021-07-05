package com.github.sirblobman.combatlogx.api.expansion.region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class RegionVulnerableListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;

    public RegionVulnerableListener(RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        RegionHandler regionHandler = this.regionExpansion.getRegionHandler();
        NoEntryMode noEntryMode = regionHandler.getNoEntryMode();
        if(noEntryMode != NoEntryMode.VULNERABLE) return;

        Entity damaged = e.getEntity();
        if(!(damaged instanceof Player)) return;

        Player player = (Player) damaged;
        if(isInCombat(player)) {
            ICombatManager combatManager = getCombatManager();
            LivingEntity enemy = combatManager.getEnemy(player);
            TagType tagType = getTagType(enemy);

            Location location = player.getLocation();
            if(regionHandler.isSafeZone(player, location, tagType)) {
                e.setCancelled(false);
            }
        }
    }

    private TagType getTagType(LivingEntity enemy) {
        if(enemy == null) return TagType.UNKNOWN;
        if(enemy instanceof Player) return TagType.PLAYER;
        return TagType.MOB;
    }
}
