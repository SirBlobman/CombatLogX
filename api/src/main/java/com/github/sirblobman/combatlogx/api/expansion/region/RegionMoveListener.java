package com.github.sirblobman.combatlogx.api.expansion.region;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.TagType;

public final class RegionMoveListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;
    public RegionMoveListener(RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    @EventHandler(priority= EventPriority.NORMAL, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        Location toLocation = e.getTo();
        if(toLocation == null) return;

        Player player = e.getPlayer();
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        LivingEntity enemy = combatManager.getEnemy(player);
        TagType tagType = (enemy == null ? TagType.UNKNOWN : (enemy instanceof Player ? TagType.PLAYER : TagType.MOB));

        RegionHandler regionHandler = this.regionExpansion.getRegionHandler();
        if(regionHandler.isSafeZone(player, toLocation, tagType)) {
            Location fromLocation = e.getFrom();
            regionHandler.preventEntry(e, player, fromLocation, toLocation);
        }
    }
}