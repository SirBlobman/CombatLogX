package com.SirBlobman.worldguard;

import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardUtil extends Util {
    public static WorldGuardPlugin worldGuard() {
        Plugin pl = PM.getPlugin("WorldGuard");
        if(pl == null || !(pl instanceof WorldGuardPlugin)) return null;
        else {
            WorldGuardPlugin wgp = (WorldGuardPlugin) pl;
            return wgp;
        }
    }
    
    public static State getState(Player p, StateFlag sf) {
        Location l = p.getLocation();
        State s = getState(l, sf);
        return s;
    }
    
    public static State getState(Location l, StateFlag sf) {
        WorldGuardPlugin wg = worldGuard();
        RegionContainer rc = wg.getRegionContainer();
        RegionQuery rq = rc.createQuery();
        RegionAssociable ra = null;
        State s = rq.queryValue(l, ra, sf);
        return s;
    }
    
    public static boolean pvp(Player p) {
        Location l = p.getLocation();
        return pvp(l);
    }
    
    public static boolean pvp(Location l) {
        State s = getState(l, DefaultFlag.PVP);
        if(s == null) s = State.ALLOW;
        boolean pvp = (s == State.ALLOW);
        return pvp;
    }
    
    public static boolean isSafeZone(Location l) {
        boolean pvp = pvp(l);
        return !pvp;
    }
}