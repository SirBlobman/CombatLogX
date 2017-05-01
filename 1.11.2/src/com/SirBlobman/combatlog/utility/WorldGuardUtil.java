package com.SirBlobman.combatlog.utility;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.SirBlobman.combatlog.config.Config;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardUtil extends Util {
	public static WorldGuardPlugin wg() {
		if(Config.WORLD_GUARD) {
			Plugin pl = PM.getPlugin("WorldGuard");
			if(pl == null) return null;
			if(pl instanceof WorldGuardPlugin) {
				WorldGuardPlugin wg = (WorldGuardPlugin) pl;
				return wg;
			} else {
				Config.WORLD_GUARD = false;
				String error = "Invalid World Guard version!";
				print(error);
				return null;
			}
		} else return null;
	}
	
	public static ApplicableRegionSet regions(Player p) {
		Location l = p.getLocation();
		ApplicableRegionSet set = regions(l);
		return set;
	}
	
	public static ApplicableRegionSet regions(Location l) {
		World w = l.getWorld();
		WorldGuardPlugin wg = wg();
		RegionManager rm = wg.getRegionManager(w);
		ApplicableRegionSet set = rm.getApplicableRegions(l);
		return set;
	}
	
	public static boolean canPvp(Player p) {
		if(p == null) return false;
		Location l = p.getLocation();
		World w = l.getWorld();
		if(Config.WORLD_GUARD) {
			try {
				WorldGuardPlugin wg = wg();
				LocalPlayer lp = wg.wrapPlayer(p);
				ApplicableRegionSet set = regions(p);
				StateFlag PVP = DefaultFlag.PVP;
				State s = set.queryState(lp, PVP);
				if(s != null) {
					boolean pvp = set.testState(lp, PVP);
					return pvp;
				} else {
					boolean pvp = w.getPVP();
					return pvp;
				}
			} catch(Throwable ex) {
				String error = "Failed to execute a WorldGuard Task:\n" + ex.getCause();
				Util.print(error);
				boolean pvp = w.getPVP();
				return pvp;
			}
		} else {
			boolean pvp = w.getPVP();
			return pvp;
		}
	}
}