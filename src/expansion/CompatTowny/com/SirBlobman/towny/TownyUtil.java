package com.SirBlobman.towny;

import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.object.*;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class TownyUtil {
    public static boolean pvp(Player p) {
        Location l = p.getLocation();
        return pvp(l);
    }
    
    public static boolean pvp(Location l) {
        World w = l.getWorld();
        String name = w.getName();
        TownyDataSource tds = TownyUniverse.getDataSource();
        try {
            TownyWorld tw = tds.getWorld(name);
            Coord c = Coord.parseCoord(l);
            if(tw.hasTownBlock(c)) {
                TownBlock tb = tw.getTownBlock(c);
                if(tb.hasTown()) {
                    Town t = tb.getTown();
                    boolean pvp = t.isPVP();
                    return pvp;
                } else return true;
            } else return true;
        } catch(Throwable ex) {return true;}
    }
}