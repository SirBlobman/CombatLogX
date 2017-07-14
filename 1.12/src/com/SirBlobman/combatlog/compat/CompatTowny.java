package com.SirBlobman.combatlog.compat;

import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.object.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CompatTowny implements Listener {
	public static boolean pvp(Player p) {
		World w = p.getWorld();
		String name = w.getName();
		try {
			TownyDataSource tds = TownyUniverse.getDataSource();
			TownyWorld tw = tds.getWorld(name);
			Coord c = Coord.parseCoord(p);
			if(tw.hasTownBlock(c)) {
				TownBlock tb = tw.getTownBlock(c);
				Town t = tb.getTown();
				boolean pvp = t.isPVP();
				return pvp;
			} else return w.getPVP();
		} catch(Exception ex) {return w.getPVP();}
	}
	
	public static boolean pvp(Location l) {
		World w = l.getWorld();
		String name = w.getName();
		try {
			TownyDataSource tds = TownyUniverse.getDataSource();
			TownyWorld tw = tds.getWorld(name);
			Coord c = Coord.parseCoord(l);
			if(tw.hasTownBlock(c)) {
				TownBlock tb = tw.getTownBlock(c);
				Town t = tb.getTown();
				boolean pvp = t.isPVP();
				return pvp;
			} else return w.getPVP();
		} catch(Exception ex) {return w.getPVP();}
	}
	
	public static boolean pvp(WorldCoord wc) {
		World w = wc.getBukkitWorld();
		String name = w.getName();
		try {
			TownyDataSource tds = TownyUniverse.getDataSource();
			TownyWorld tw = tds.getWorld(name);
			Coord c = wc.getCoord();
			if(tw.hasTownBlock(c)) {
				TownBlock tb = tw.getTownBlock(c);
				Town t = tb.getTown();
				boolean pvp = t.isPVP();
				return pvp;
			} else return w.getPVP();
		} catch(Exception ex) {return w.getPVP();}
	}
}