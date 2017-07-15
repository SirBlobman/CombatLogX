package com.SirBlobman.combatlogx.compat.factions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;

public class CompatFactionsLegacy {
	public static Faction factionAt(Player p) {
		FPlayerColl coll = FPlayerColl.getUnsafeInstance();
		FPlayer fp = coll.getByPlayer(p);
		FLocation fl = fp.getLastStoodAt();
		Board b = Board.get();
		Faction f = b.getFactionAt(fl);
		return f;
	}
	
	public static Faction factionAt(Location l) {
		FLocation fl = new FLocation(l);
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
	
	public static boolean pvp(Location l) {
		Faction f = factionAt(l);
		if(f.isWarZone()) return true;
		if(f.isWilderness()) return true;
		if(f.isSafeZone()) return false;
		return pvp(f);
	}
	
	public static boolean canAttack(Player p, Player t) {
		Faction fp = current(p);
		Faction ft = current(t);
		if(fp.isWilderness() || ft.isWilderness()) return true;
		else if(fp.equals(ft)) return false;
		else {
			Relation r = fp.getRelationTo(ft);
			return r.isEnemy();
		}
	}
}