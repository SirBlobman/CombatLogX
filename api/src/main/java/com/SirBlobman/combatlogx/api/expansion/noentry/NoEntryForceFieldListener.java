package com.SirBlobman.combatlogx.api.expansion.noentry;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.olivolja3.force.field.ForceField;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NoEntryForceFieldListener extends ForceField {
    private final NoEntryHandler noEntryHandler;
    public NoEntryForceFieldListener(NoEntryExpansion expansion) {
        super(expansion);
        this.noEntryHandler = expansion.getNoEntryHandler();
    }

    @Override
    public boolean isSafe(Location location, Player player, TagType tagType) {
        return this.noEntryHandler.isSafeZone(player, location, tagType);
    }

    @Override
    public boolean isSafe(Location location, Player player) {
        return this.noEntryHandler.isSafeZone(player, location);
    }
    
    @Override
    public boolean isEnabled() {
        return this.noEntryHandler.isForceFieldEnabled();
    }
    
    @Override
    public boolean canBypass(Player player) {
        return this.noEntryHandler.canBypassForceField(player);
    }

    @Override
    public Material getForceFieldMaterial() {
        return this.noEntryHandler.getForceFieldMaterial();
    }

    @Override
    public int getForceFieldMaterialData() {
        return this.noEntryHandler.getForceFieldMaterialData();
    }

    @Override
    public int getForceFieldRadius() {
        return this.noEntryHandler.getForceFieldRadius();
    }
}