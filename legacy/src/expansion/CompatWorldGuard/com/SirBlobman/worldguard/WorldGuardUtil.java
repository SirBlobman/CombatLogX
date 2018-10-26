package com.SirBlobman.worldguard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardUtil extends Util {
    public static WorldGuardPlugin worldGuard() {
        Plugin pl = PM.getPlugin("WorldGuard");
        if (pl == null || !(pl instanceof WorldGuardPlugin))
            return null;
        else {
            WorldGuardPlugin wgp = (WorldGuardPlugin) pl;
            return wgp;
        }
    }

    public static ApplicableRegionSet getRegions(Player p) {
        Location l = p.getLocation();
        ApplicableRegionSet apr = getRegions(l);
        return apr;
    }

    public static ApplicableRegionSet getRegions(Location l) {
        WorldGuardPlugin wgp = worldGuard();
        World world = l.getWorld();
        RegionManager rm = wgp.getRegionManager(world);
        ApplicableRegionSet apr = rm.getApplicableRegions(l);
        return apr;
    }

    public static boolean isSafeZone(Location to) {
        ApplicableRegionSet apr = getRegions(to);
        State state = apr.queryValue(null, DefaultFlag.PVP);
        if(state == State.DENY) return true;
        else return false;
    }

    public static boolean isSafeFromMobs(Location to) {
        ApplicableRegionSet apr = getRegions(to);
        State state = apr.queryValue(null, DefaultFlag.MOB_SPAWNING);
        if(state == State.DENY) return true;
        else return false;
    }

    /*
     * Anything below this is from the `utils.java` file that olivolja3 made
     */
    public static String getMaterial(String material) {
        String[] m;
        if (material.contains(":")) {
            m = material.split(":");
            return m[0];
        } else return material;
    }

    public static byte getData(String material) {
        String[] m;
        if (material.contains(":")) {
            m = material.split(":");
            return (byte) Integer.parseInt(m[1]);
        } else return 0;
    }

}