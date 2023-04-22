package com.github.sirblobman.combatlogx.api.expansion.region;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class RegionExpansionListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;

    public RegionExpansionListener(@NotNull RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    protected final @NotNull RegionExpansion getRegionExpansion() {
        return this.regionExpansion;
    }

    protected final @NotNull RegionHandler getRegionHandler() {
        RegionExpansion regionExpansion = getRegionExpansion();
        return regionExpansion.getRegionHandler();
    }
}
