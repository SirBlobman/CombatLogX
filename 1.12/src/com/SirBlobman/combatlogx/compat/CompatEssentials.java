package com.SirBlobman.combatlogx.compat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CompatEssentials {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();
	
	public static Essentials getEssentials() {
		Plugin pl = PM.getPlugin("Essentials");
		if(pl == null || !(pl instanceof Essentials)) return null;
		else return (Essentials) pl;
	}
	
	public static User getUser(Player p) {
		Essentials e = getEssentials();
		User u = e.getUser(p);
		return u;
	}
	
	public static boolean hasGod(Player p) {
		User u = getUser(p);
		boolean god = u.isGodModeEnabled();
		return god;
	}
}