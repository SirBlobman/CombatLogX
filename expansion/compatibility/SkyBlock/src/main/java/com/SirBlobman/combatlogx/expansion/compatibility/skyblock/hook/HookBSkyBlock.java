package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

public class HookBSkyBlock extends SkyBlockHook {
    @Override
    public boolean doesTeamMatch(Player player1, Player player2) {
        if(player1 == null || player2 == null) return false;
        
        World world1 = player1.getWorld();
        World world2 = player2.getWorld();
        UUID worldId1 = world1.getUID();
        UUID worldId2 = world2.getUID();
        if(!worldId1.equals(worldId2)) return false;
        
        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;
        
        Island island = getIslandFor(player1);
        ImmutableSet<UUID> memberSet = island.getMemberSet();
        return memberSet.contains(uuid2);
    }
    
    @Override
    public Island getIslandFor(Player player) {
        if(player == null) return null;
        UUID uuid = player.getUniqueId();
        
        Addon addon = HookBentoBox.getBSkyBlock();
        IslandsManager manager = addon.getIslands();
        
        World world = player.getWorld();
        return manager.getIsland(world, uuid);
    }
    
    @Override
    public Island getIslandAt(Location location) {
        if(location == null) return null;
        
        Addon addon = HookBentoBox.getBSkyBlock();
        IslandsManager manager = addon.getIslands();

        Optional<Island> optional = manager.getIslandAt(location);
        return optional.orElse(null);
    }
}