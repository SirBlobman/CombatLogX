package com.SirBlobman.worldguard;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorldGuardUtil extends Util {
    public static WorldGuardPlugin worldGuard() {
        Plugin pl = PM.getPlugin("WorldGuard");
        if(pl == null || !(pl instanceof WorldGuardPlugin)) return null;
        else {
            WorldGuardPlugin wgp = (WorldGuardPlugin) pl;
            return wgp;
        }
    }
    
    public static List<ProtectedRegion> getRegions(Player p) {
        Location l = p.getLocation();
        List<ProtectedRegion> list = getRegions(l);
        return list;
    }
    
    public static List<ProtectedRegion> getRegions(Location l) {
        WorldGuardPlugin wgp = worldGuard();
        World world = l.getWorld();
        RegionManager rm = wgp.getRegionManager(world);
        ApplicableRegionSet apr = rm.getApplicableRegions(l);
        List<ProtectedRegion> list = newList(apr.getRegions());
        return list;
    }
    
    public static boolean isSafeZone(Location to) {
        List<ProtectedRegion> list = getRegions(to);
        for(ProtectedRegion pr : list) {
            if(isSafeZone(pr)) return true;
            else continue;
        } return false;
    }
    
    public static boolean isSafeZone(ProtectedRegion pr) {
        Map<Flag<?>, Object> map = pr.getFlags();
        Set<Flag<?>> flags = map.keySet();
        if(flags.contains(DefaultFlag.PVP)) {
            State state = pr.getFlag(DefaultFlag.PVP);
            return (state == State.DENY);
        } else return false;
    }
    
    public static Location getCenter(World world, ProtectedRegion pr) {
        Vector c1 = pr.getMaximumPoint();
        Vector c2 = pr.getMinimumPoint();
        Vector center = Vector.getMidpoint(c1, c2);
        
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        
        Location loc = new Location(world, x, y, z);
        return loc;
    }
    
    public static org.bukkit.util.Vector getKnockbackVector(Location ploc) {
        List<ProtectedRegion> list = getRegions(ploc);
        ProtectedRegion safeZone = null;
        for(ProtectedRegion pr : list) {
            if(isSafeZone(pr)) {
                safeZone = pr;
                break;
            } else continue;
        }
        
        if(safeZone != null) {
            World world = ploc.getWorld();
            Location center = getCenter(world, safeZone);
            org.bukkit.util.Vector from = center.toVector();
            org.bukkit.util.Vector to = ploc.toVector();
            org.bukkit.util.Vector vector = to.subtract(from);
            vector.multiply(ConfigOptions.CHEAT_PREVENT_NO_ENTRY_STRENGTH);
            vector.setY(0);
            return vector;
        } else return new org.bukkit.util.Vector(0, 0, 0);
    }
}