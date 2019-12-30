package com.SirBlobman.combatlogx.expansion.compatibility.towny.hook;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

public final class HookTowny {
    public static TownyAPI getAPI() {
        return TownyAPI.getInstance();
    }

    public static TownBlock getTownBlock(Location location) {
        TownyAPI api = getAPI();
        if(api == null) return null;

        return api.getTownBlock(location);
    }

    public static Town getTown(Location location) {
        try {
            TownBlock townBlock = getTownBlock(location);
            if(townBlock == null) return null;

            return townBlock.getTown();
        } catch(NotRegisteredException ex) {
            return null;
        }
    }

    public static boolean isSafeZone(Location location) {
        Town town = getTown(location);
        if(town == null) return false;

        return !town.isPVP();
    }
}