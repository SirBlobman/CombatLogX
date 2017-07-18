package com.SirBlobman.combatlogx.compat.worldguard;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CompatWorldGuard implements Listener {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();
	public static WorldGuardPlugin wg() {
		Plugin p = PM.getPlugin("WorldGuard");
		if(p == null || !(p instanceof WorldGuardPlugin)) return null;
		else return (WorldGuardPlugin) p;
	}
	
	public static boolean allows(@Nullable Player p, @Nonnull StateFlag sf) {
		Location l = p.getLocation();
		WorldGuardPlugin wg = wg();
		RegionContainer rc = wg.getRegionContainer();
		RegionQuery rq = rc.createQuery();
		State s = rq.queryValue(l, p, sf);
		if(s == null && sf.getDefault() == State.ALLOW) return true;
		boolean a = (s == State.ALLOW);
		return a;
	}
	
	public static boolean getBoolean(@Nullable Player p, @Nonnull BooleanFlag bf) {
		Location l = p.getLocation();
		WorldGuardPlugin wg = wg();
		RegionContainer rc = wg.getRegionContainer();
		RegionQuery rq = rc.createQuery();
		boolean b = rq.queryValue(l, p, bf);
		return b;
	}
	
	public static boolean pvp(Player p) {
		boolean pvp = allows(p, DefaultFlag.PVP);
		return pvp;
	}
	
	public static boolean pvp(Location l) {
		boolean pvp = allows(null, DefaultFlag.PVP);
		return pvp;
	}
}