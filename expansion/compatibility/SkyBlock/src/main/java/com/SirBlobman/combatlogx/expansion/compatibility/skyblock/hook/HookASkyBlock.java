package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

public class HookASkyBlock extends SkyBlockHook {
    @Override
    public boolean doesTeamMatch(Player player1, Player player2) {
        if(player1 == null || player2 == null) return false;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;

        Island island = getIslandFor(player1);
        if(island == null) return false;

        List<UUID> memberList = island.getMembers();
        return memberList.contains(uuid2);
    }

    @Override
    public Island getIslandFor(Player player) {
        if(player == null) return null;
        UUID uuid = player.getUniqueId();

        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        return api.getIslandOwnedBy(uuid);
    }

    @Override
    public Island getIslandAt(Location location) {
        if(location == null) return null;

        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        return api.getIslandAt(location);
    }
}