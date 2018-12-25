package com.SirBlobman.expansion.compatfactions.util;

import org.bukkit.Location;

import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;

public class FactionsUtilLegacy extends FactionsUtil {
    @Override
    public Faction getFaction(Location loc) {
        Locality locality = Locality.of(loc);
        return locality.getFactionHere();
    }

    @Override
    public boolean isSafeZone(Location loc) {
        Faction faction = getFaction(loc);
        return (faction.isSafeZone() || faction.noPvPInTerritory());
    }
}