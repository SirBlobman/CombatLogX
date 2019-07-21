package com.SirBlobman.expansion.worldguard.utility.v7_0;

import org.bukkit.Location;

import com.SirBlobman.combatlogx.utility.Util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class v7_0_WGUtil extends Util {
    private static final StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);
    private static final BooleanFlag NO_TAG = new BooleanFlag("no-tagging");
    
    private static WorldGuardPlatform getAPI() {
        return WorldGuard.getInstance().getPlatform();
    }
    
    public static void registerFlags() {
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        if(flagRegistry.get("mob-combat") == null) flagRegistry.register(MOB_COMBAT);
        if(flagRegistry.get("no-tag") == null) flagRegistry.register(NO_TAG);
    }
    
    public static boolean allowsPvP(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);
        
        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        
        StateFlag.State state = rq.queryState(worldEditLoc, null, Flags.PVP);
        return (state != StateFlag.State.DENY);
    }
    
    public static boolean allowsMobCombat(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);
        
        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        
        StateFlag.State state = rq.queryState(worldEditLoc, null, MOB_COMBAT);
        if(state == null) return true;
        return (state != StateFlag.State.DENY);
    }
    
    public static boolean allowsTagging(Location loc) {
        com.sk89q.worldedit.util.Location worldEditLoc = BukkitAdapter.adapt(loc);
        
        WorldGuardPlatform api = getAPI();
        RegionContainer rc = api.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        
        Boolean noTagging = rq.queryValue(worldEditLoc, null, NO_TAG);
        return (noTagging == null || !noTagging);
    }
}