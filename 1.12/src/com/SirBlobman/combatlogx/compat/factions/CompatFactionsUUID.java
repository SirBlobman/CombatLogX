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
	
	public static boolean pvp(Location l) {
		Faction f = factionAt(l);
		return !f.noPvPInTerritory();
	}
	
	public static boolean canAttack(Player p, Player t) {
		Faction fp = getFaction(p);
		Faction ft = getFaction(t);
		if(fp.isWilderness() || ft.isWilderness()) return true;
		if(fp.equals(ft)) return false;
		else {
			Relation rel = fp.getRelationTo(ft);
			boolean en = rel.isEnemy();
			return en;
		}
	}
}