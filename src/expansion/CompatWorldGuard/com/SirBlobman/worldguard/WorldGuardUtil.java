package com.SirBlobman.worldguard;

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
        int priority = -1;
        boolean safeZone = false;
        for(ProtectedRegion pr : list) {
            int npri = pr.getPriority();
            if(npri > priority) {
                boolean safe = isSafeZone(pr);
                safeZone = safe;
            } else continue;
        } return safeZone;
    }
    
    public static boolean isSafeZone(ProtectedRegion pr) {
        Map<Flag<?>, Object> map = pr.getFlags();
        Set<Flag<?>> flags = map.keySet();
        if(flags.contains(DefaultFlag.PVP)) {
            State state = pr.getFlag(DefaultFlag.PVP);
            if(state == null || state == State.ALLOW) return false;
            else return true;
        } else return false;
    }
    
    public static boolean isSafeFromMobs(Location to) {
        List<ProtectedRegion> list = getRegions(to);
        int priority = -1;
        boolean safezone = false;
        for(ProtectedRegion pr : list) {
            int npri = pr.getPriority();
            if(npri > priority) {
                boolean safe = isSafeFromMobs(pr);
                safezone = safe;
            } else continue;
        } return safezone;
    }
    
    public static boolean isSafeFromMobs(ProtectedRegion pr) {
        Map<Flag<?>, Object> map = pr.getFlags();
        Set<Flag<?>> flags = map.keySet();
        if(flags.contains(DefaultFlag.MOB_SPAWNING)) {
            State state = pr.getFlag(DefaultFlag.MOB_SPAWNING);
            if(state == null || state == State.ALLOW) return false;
            else return true;
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
    
    public static org.bukkit.util.Vector getMobsZoneKnockbackVector(Location ploc) {
        List<ProtectedRegion> list = getRegions(ploc);
        ProtectedRegion safeZone = null;
        for(ProtectedRegion pr : list) {
            if(isSafeFromMobs(pr)) {
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
            
            double x = vector.getX();
            double z = vector.getZ();
            
            double nx = 0;
            double nz = 0;
            if(x != 0 && x > 0) nx = 1; else nx = -1;
            if(z != 0 && z > 0) nz = 1; else nz = -1;
            
            org.bukkit.util.Vector unit = new org.bukkit.util.Vector(nx, 0, nz);
            return unit;
        } else return new org.bukkit.util.Vector(0, 0, 0);
    }
    
    public static org.bukkit.util.Vector getSafeZoneKnockbackVector(Location ploc) {
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
            
            double x = vector.getX();
            double z = vector.getZ();
            
            double nx = 0;
            double nz = 0;
            if(x != 0 && x > 0) nx = 1; else nx = -1;
            if(z != 0 && z > 0) nz = 1; else nz = -1;
            
            org.bukkit.util.Vector unit = new org.bukkit.util.Vector(nx, 0, nz);
            return unit;
        } else return new org.bukkit.util.Vector(0, 0, 0);
    }
}