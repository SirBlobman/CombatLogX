package com.SirBlobman.combatlogx.expansion.compatibility.skyblock.hook;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

public class HookUltimateSkyBlock extends SkyBlockHook {
    @Override
    public boolean doesTeamMatch(Player player1, Player player2) {
        if(player1 == null || player2 == null) return false;
        
        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;
        
        IslandInfo island = getIslandFor(player1);
        Set<String> memberNameSet = island.getMembers();
        
        String player2Name = player2.getName();
        return memberNameSet.contains(player2Name);
    }
    
    @Override
    public IslandInfo getIslandFor(Player player) {
        if(player == null) return null;
        
        uSkyBlockAPI api = getAPI();
        return api.getIslandInfo(player);
    }
    
    @Override
    public IslandInfo getIslandAt(Location location) {
        if(location == null) return null;
        
        uSkyBlockAPI api = getAPI();
        return api.getIslandInfo(location);
    }
    
    public uSkyBlockAPI getAPI() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin("uSkyBlock");
        return (uSkyBlockAPI) plugin;
    }
}