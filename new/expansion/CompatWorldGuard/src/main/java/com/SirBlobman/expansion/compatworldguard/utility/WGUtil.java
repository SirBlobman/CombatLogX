package com.SirBlobman.expansion.compatworldguard.utility;

import java.lang.reflect.Method;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WGUtil extends Util {
	public static WorldGuard getAPI() {
		if(PluginUtil.isEnabled("WorldGuard")) {
			WorldGuard wg = WorldGuard.getInstance();
			return wg;
		} else return null;
	}
	
	public static StateFlag MOB_COMBAT = new StateFlag("mob-combat", false);
	public static void onLoad() {
		WorldGuard api = getAPI();
		SimpleFlagRegistry fr = (SimpleFlagRegistry) api.getFlagRegistry();
		try {
			Flag<?> flag = fr.get("mob-combat");
			if(flag == null) {
				Class<?> clazz = fr.getClass();
				Method[] methods = clazz.getDeclaredMethods();
				for(Method method : methods) {
					String name = method.getName();
					if(name.equals("forceRegister")) {
						method.setAccessible(true);
						MOB_COMBAT = (StateFlag) method.invoke(fr, MOB_COMBAT);
						method.setAccessible(false);
						break;
					}
				}
			} else if(flag instanceof StateFlag) {
				MOB_COMBAT = (StateFlag) flag;
			} else {
				String error = "Failed to create mob-combat WorldGuard flag";
				Util.print(error);
			}
		} catch(Throwable ex) {
			String error = "Failed to create mob-combat WorldGuard flag";
			Util.print(error);
			ex.printStackTrace();
		}
	}
	
	public static World getWorld(org.bukkit.World world) {
		World adapt = BukkitAdapter.adapt(world);
		return adapt;
	}
	
	public static Vector getLocation(org.bukkit.Location loc) {
		Location loc2 = BukkitAdapter.adapt(loc);
		Vector v = loc2.toVector();
		return v;
	}
	
	public static ApplicableRegionSet getRegions(org.bukkit.Location loc) {
		WorldGuard api = getAPI();
		WorldGuardPlatform wgp = api.getPlatform();
		RegionContainer rc = wgp.getRegionContainer();
		
		World world = getWorld(loc.getWorld());
		RegionManager rm = rc.get(world);
		
		Vector vector = getLocation(loc);
		ApplicableRegionSet set = rm.getApplicableRegions(vector);
		return set;
	}
	
	public static boolean allowsPvP(org.bukkit.Location loc) {
		ApplicableRegionSet regions = getRegions(loc);
		State state = regions.queryState(null, Flags.PVP);
		return (state != State.DENY);
	}
	
	public static boolean allowsMobCombat(org.bukkit.Location loc) {
		ApplicableRegionSet regions = getRegions(loc);
		State state = regions.queryState(null, MOB_COMBAT);
		return (state != State.DENY);
	}
}