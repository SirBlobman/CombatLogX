package com.SirBlobman.towny;

import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.object.*;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class TownyUtil {
    public static TownyWorld getTownWorld(Location l) {
        World world = l.getWorld();
        String name = world.getName();
        TownyDataSource tds = TownyUniverse.getDataSource();
        try {
            TownyWorld tw = tds.getWorld(name);
            return tw;
        } catch (Throwable ex) {
            return null;
        }
    }
    
    public static TownBlock getTownBlock(Location l) {
        TownyWorld tw = getTownWorld(l);
        Coord coord = Coord.parseCoord(l);
        if (tw != null && tw.hasTownBlock(coord)) {
            try {
                TownBlock tb = tw.getTownBlock(coord);
                return tb;
            } catch (Throwable ex) {
                return null;
            }
        } else return null;
    }
    
    public static Town getTown(Location l) {
        TownBlock tb = getTownBlock(l);
        if (tb != null && tb.hasTown()) {
            try {
                Town town = tb.getTown();
                return town;
            } catch (Throwable ex) {
                return null;
            }
        } else return null;
    }
    
    public static boolean pvp(Player p) {
        Location l = p.getLocation();
        return pvp(l);
    }
    
    public static boolean pvp(Location l) {
        Town town = getTown(l);
        if (town != null) {
            boolean pvp = town.isPVP();
            return pvp;
        } else return true;
    }
}