package com.SirBlobman.combatlog.utility;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardUtil extends Util {
	public static WorldGuardPlugin wg() {
		Plugin p = PM.getPlugin("WorldGuard");
		if(p == null || !(p instanceof WorldGuardPlugin)) return null;
		else return (WorldGuardPlugin) p;
	}
	
	public static boolean pvp(Player p) {
		WorldGuardPlugin wg = wg();
		Location l = p.getLocation();
		RegionContainer rc = wg.getRegionContainer();
		RegionQuery rq = rc.createQuery();
		State s = rq.queryValue(l, p, DefaultFlag.PVP);
		if(s == null) return true;
		boolean pvp = (s == State.ALLOW);
		return pvp;
	}
	
	public static boolean pvp(Location l) {
		WorldGuardPlugin wg = wg();
		RegionContainer rc = wg.getRegionContainer();
		RegionQuery rq = rc.createQuery();
		RegionAssociable ra = null;
		State s = rq.queryValue(l, ra, DefaultFlag.PVP);
		if(s == null) return true;
		boolean pvp = (s == State.ALLOW);
		return pvp;
	}
}