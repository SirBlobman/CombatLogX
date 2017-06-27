package com.SirBlobman.combatlog.compat;

import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

public class CompatLegacyFactions {
	public static Faction factionAt(Player p) {
		FPlayerColl coll = FPlayerColl.getUnsafeInstance();
		FPlayer fp = coll.getByPlayer(p);
		FLocation fl = fp.getLastStoodAt();
		Board b = Board.get();
		Faction f = b.getFactionAt(fl);
		return f;
	}
	
	public static Faction current(Player p) {
		FPlayerColl coll = FPlayerColl.getUnsafeInstance();
		FPlayer fp = coll.getByPlayer(p);
		Faction f = fp.getFaction();
		return f;
	}
	
	public static boolean pvp(Faction f) {
		boolean pvp = !f.noPvPInTerritory();
		return pvp;
	}
	
	public static boolean canPVP(Player p) {
		try {
			Faction f = factionAt(p);
			Faction c = current(p);
			if(f.isWilderness()) return true;
			if(f.isWarZone()) return true;
			if(f.equals(c)) return false;
			return pvp(f);
		} catch(Throwable ex) {
			World w = p.getWorld();
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
}