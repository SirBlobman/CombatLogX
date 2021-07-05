package com.github.sirblobman.combatlogx.api.expansion.region;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class RegionListener extends ExpansionListener {
    private final RegionExpansion regionExpansion;

    public RegionListener(RegionExpansion expansion) {
        super(expansion);
        this.regionExpansion = expansion;
    }

    protected final RegionExpansion getRegionExpansion() {
        return this.regionExpansion;
    }

    protected final RegionHandler getRegionHandler() {
        RegionExpansion regionExpansion = getRegionExpansion();
        return regionExpansion.getRegionHandler();
    }
}
