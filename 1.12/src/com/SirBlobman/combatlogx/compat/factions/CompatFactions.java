package com.SirBlobman.combatlogx.compat.factions;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.ps.PS;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CompatFactions {
	private static final FactionColl FC = FactionColl.get();
	private static final BoardColl BC = BoardColl.get();
	private static final Faction WILDERNESS = FC.getNone();
	
	public static Faction standingIn(Player p) {
		Location l = p.getLocation();
		return factionAt(l);
	}
	
	public static Faction getFaction(Player p) {
		MPlayer mp = MPlayer.get(p);
		Faction f = mp.getFaction();
		return f;
	}
	
	public static Faction factionAt(Location l) {
		PS ps = PS.valueOf(l);
		Faction f = BC.getFactionAt(ps);
		return f;
	}
	
	public static boolean pvp(Location l) {
		Faction f = factionAt(l);
		MFlag mf = MFlag.getFlagPvp();
		boolean pvp = f.getFlag(mf);
		return pvp;
	}
	
	public static boolean canAttack(Player p, Player t) {
		Faction fp = getFaction(p);
		Faction ft = getFaction(t);
		if(fp.equals(WILDERNESS) || ft.equals(WILDERNESS)) return true;
		if(fp.equals(ft)) {
			String pvp = MFlag.ID_FRIENDLYFIRE;
			boolean can = fp.getFlag(pvp);
			return can;
		} else {
			Rel rel = fp.getRelationTo(ft);
			boolean can = !rel.isFriend();
			return can;
		}
	}
}