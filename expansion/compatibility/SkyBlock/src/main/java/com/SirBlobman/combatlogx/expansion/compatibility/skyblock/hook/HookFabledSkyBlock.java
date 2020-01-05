package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandManager;

public class HookFabledSkyBlock extends SkyBlockHook {
    @Override
    public boolean doesTeamMatch(Player player1, Player player2) {
        if(player1 == null || player2 == null) return false;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;

        Island island1 = getIslandFor(player1);
        if(island1 == null) return false;

        Island island2 = getIslandFor(player2);
        if(island2 == null) return false;

        UUID islandId1 = island1.getIslandUUID();
        UUID islandId2 = island2.getIslandUUID();
        return islandId1.equals(islandId2);
    }

    @Override
    public Island getIslandFor(Player player) {
        if(player == null) return null;

        IslandManager manager = SkyBlockAPI.getIslandManager();
        return manager.getIsland(player);
    }

    @Override
    public Island getIslandAt(Location location) {
        if(location == null) return null;

        IslandManager manager = SkyBlockAPI.getIslandManager();
        return manager.getIslandAtLocation(location);
    }
}