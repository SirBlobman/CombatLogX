package com.SirBlobman.combatlogx.compat.factions;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CompatFactionsUUID {
	private static final FPlayers FP = FPlayers.getInstance();
	private static final Board BOARD = Board.getInstance();
	
	public static Faction standingIn(Player p) {
		Location l = p.getLocation();
		Faction f = factionAt(l);
		return f;
	}
	
	public static Faction getFaction(Player p) {
		FPlayer fp = FP.getByPlayer(p);
		Faction f = fp.getFaction();
		return f;
	}
	
	public static Faction factionAt(Location l) {
		FLocation fl = new FLocation(l);
		Faction f = BOARD.getFactionAt(fl);
		return f;
	}
	
	public static boolean pvp(Faction f) {
		boolean pvp = !f.noPvPInTerritory();
		return pvp;
	}
	
	public static boolean pvp(Player p) {
		Faction f = getFaction(p);
		Faction s = standingIn(p);
		if(s.isWilderness()) return true;
		else if(s.isWarZone()) return true;
		else if(s.isSafeZone()) return false;
		else if(f.equals(s)) return pvp(s);
		else return true;
	}
	
	public static boolean pvp(Location l) {
		Faction f = factionAt(l);
		if(f.isWilderness()) return true;
		else if(f.isWarZone()) return true;
		else if(f.isSafeZone()) return false;
		else return pvp(f);
	}
	
	public static boolean canAttack(Player p, Player t) {
		Faction fp = getFaction(p);
		Faction ft = getFaction(t);
		if(fp.isWilderness() || ft.isWilderness()) return true;
		if(fp.equals(ft)) return pvp(fp);
		else {
			Relation r = fp.getRelationTo(ft);
			boolean can = r.isEnemy();
			return can;
		}
	}
}