package com.SirBlobman.combatlogx.expansion.compatibility.residence.hook;

import org.bukkit.Location;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.api.ResidenceInterface;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

public final class HookResidence {
    public static boolean isSafeZone(Location location) {
        ResidenceInterface manager = ResidenceApi.getResidenceManager();
        if(manager == null) return false;

        ClaimedResidence claim = manager.getByLoc(location);
        if(claim == null) return false;

        ResidencePermissions permissions = claim.getPermissions();
        return !permissions.has(Flags.pvp, true);
    }
}