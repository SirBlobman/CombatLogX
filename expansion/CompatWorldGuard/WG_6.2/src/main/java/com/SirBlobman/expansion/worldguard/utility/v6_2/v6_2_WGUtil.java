package com.SirBlobman.expansion.worldguard.utility.v6_2;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class v6_2_WGUtil {
    private static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);
    private static BooleanFlag NO_TAG = new BooleanFlag("no-tagging");
    
    private static WorldGuardPlugin getAPI() {
        return JavaPlugin.getPlugin(WorldGuardPlugin.class);
    }
    
    public static void registerMobCombatFlag() {
        WorldGuardPlugin api = getAPI();
        api.getFlagRegistry().register(MOB_COMBAT);
    }
    
    public static void registerNoTagFlag() {
        WorldGuardPlugin api = getAPI();
        api.getFlagRegistry().register(NO_TAG);
    }
    
    private static ApplicableRegionSet getRegions(Location loc) {
        WorldGuardPlugin api = getAPI();
        
        World world = loc.getWorld();
        RegionManager rm = api.getRegionManager(world);
        
        return rm.getApplicableRegions(loc);
    }
    
    public static boolean allowsPvP(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        StateFlag.State state = regions.queryState(null, DefaultFlag.PVP);
        return (state != StateFlag.State.DENY);
    }
    
    public static boolean allowsMobCombat(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        StateFlag.State state = regions.queryState(null, MOB_COMBAT);
        if(state == null) return true;
        return (state != StateFlag.State.DENY);
    }
    
    public static boolean allowsTagging(Location loc) {
        ApplicableRegionSet regions = getRegions(loc);
        boolean noTagging = regions.queryValue(null, NO_TAG);
        return !noTagging;
    }
}