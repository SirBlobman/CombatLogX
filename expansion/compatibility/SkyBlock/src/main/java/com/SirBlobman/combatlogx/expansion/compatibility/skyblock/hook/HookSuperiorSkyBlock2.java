package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.handlers.GridManager;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class HookSuperiorSkyBlock2 extends SkyBlockHook {
    @Override
    public boolean doesTeamMatch(Player player1, Player player2) {
        if(player1 == null || player2 == null) return false;
        
        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;
        
        Island island = getIslandFor(player1);
        if(island == null) return false;

        List<SuperiorPlayer> memberList = island.getIslandMembers(true);
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player2);
        return memberList.contains(superiorPlayer);
    }

    @Override
    public Island getIslandFor(Player player) {
        if(player == null) return null;
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        return superiorPlayer.getIsland();
    }

    @Override
    public Island getIslandAt(Location location) {
        if(location == null) return null;
        GridManager gridManager = SuperiorSkyblockAPI.getGrid();
        return gridManager.getIslandAt(location);
    }
}