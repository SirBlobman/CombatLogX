package com.github.sirblobman.combatlogx.api.expansion.region.listener;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.expansion.region.configuration.RegionExpansionConfiguration;

public abstract class RegionExpansionListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;

    public RegionExpansionListener(@NotNull RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    protected final @NotNull RegionExpansion getRegionExpansion() {
        return this.regionExpansion;
    }

    protected final @NotNull RegionExpansionConfiguration getConfiguration() {
        RegionExpansion expansion = getRegionExpansion();
        return expansion.getConfiguration();
    }

    protected final @NotNull RegionHandler<?> getRegionHandler() {
        RegionExpansion regionExpansion = getRegionExpansion();
        return regionExpansion.getRegionHandler();
    }
}
