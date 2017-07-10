package com.SirBlobman.combatlog.utility;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

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
		LocalPlayer lp = wg.wrapPlayer(p);
		Location l = p.getLocation();
		RegionContainer rc = wg.getRegionContainer();
		RegionQuery rq = rc.createQuery();
		boolean pvp = rq.testState(l, lp, DefaultFlag.PVP);
		return pvp;
	}
}