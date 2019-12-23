package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.locality.Locality;

public class HookLegacyFactions extends FactionsHook {
    @Override
    public Faction getFactionAt(Location location) {
        if(location == null) return null;

        Locality locality = Locality.of(location);
        return locality.getFactionHere();
    }

    @Override
    public Faction getFactionFor(OfflinePlayer offline) {
        if(offline == null) return null;

        FPlayer fplayer = FPlayerColl.get(offline);
        return fplayer.getFaction();
    }

    @Override
    public boolean isSafeZone(Location location) {
        Faction faction = getFactionAt(location);
        if(faction == null) return false;

        return faction.isSafeZone();
    }
}