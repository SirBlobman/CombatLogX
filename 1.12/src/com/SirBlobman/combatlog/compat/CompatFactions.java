package com.SirBlobman.combatlog.compat;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

public class CompatFactions {
	public static Faction factionAt(Player p) {
		FPlayers list = FPlayers.getInstance();
		FPlayer fp = list.getByPlayer(p);
		FLocation fl = fp.getLastStoodAt();
		Board b = Board.getInstance();
		Faction f = b.getFactionAt(fl);
		return f;
	}
	
	public static Faction current(Player p) {
		FPlayers list = FPlayers.getInstance();
		FPlayer fp = list.getByPlayer(p);
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
			if(f.equals(c)) return false;
			return pvp(f);
		} catch(Throwable ex) {
			World w = p.getWorld();
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
}