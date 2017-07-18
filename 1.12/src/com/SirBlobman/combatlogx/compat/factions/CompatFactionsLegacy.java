package com.SirBlobman.combatlogx.compat.factions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.Relation;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;

public class CompatFactionsLegacy {
	public static Faction factionAt(Player p) {
		Location l = p.getLocation();
		return factionAt(l);
	}

	public static Faction factionAt(Location l) {
		FLocation fl = new FLocation(l);
		Board b = Board.get();
		Faction f = b.getFactionAt(fl);
		return f;
	}

	public static Faction current(Player p) {
		Faction f = FactionColl.get(p);
		return f;
	}

	public static boolean pvp(Location l) {
		Faction f = factionAt(l);
		return !f.noPvPInTerritory();
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