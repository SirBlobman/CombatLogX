package com.SirBlobman.combatlogx.expansion.compatibility.towny.hook;

import org.bukkit.Location;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;

public final class HookTowny {
    public static TownyAPI getAPI() {
        return TownyAPI.getInstance();
    }

    public static TownBlock getTownBlock(Location location) {
        TownyAPI api = getAPI();
        if(api == null) return null;

        return api.getTownBlock(location);
    }
    
    public static TownyWorld getTownWorld(Location location) {
        TownBlock townBlock = getTownBlock(location);
        return (townBlock == null ? null : townBlock.getWorld());
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
        TownyWorld townyWorld = getTownWorld(location);
        if(townyWorld == null) return false;
        if(townyWorld.isForcePVP()) return false;
        
        Town town = getTown(location);
        return (town != null && !town.isPVP());
    }
}