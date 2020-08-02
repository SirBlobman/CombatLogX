package com.SirBlobman.combatlogx.expansion.compatibility.griefprevention.hook;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public final class HookGriefPrevention {
    public static GriefPrevention getAPI() {
        return JavaPlugin.getPlugin(GriefPrevention.class);
    }

    public static boolean isSafeZone(Location location) {
        GriefPrevention api = getAPI();
        Claim claim = api.dataStore.getClaimAt(location, false, null);

        return (claim != null);
    }
}