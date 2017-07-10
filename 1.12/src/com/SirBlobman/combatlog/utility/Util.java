package com.SirBlobman.combatlog.utility;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.CombatLogX;
import com.SirBlobman.combatlog.Update;
import com.SirBlobman.combatlog.compat.CombatPlaceHolders;
import com.SirBlobman.combatlog.compat.CustomBoss;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.listener.ListenBukkit;
import com.SirBlobman.combatlog.listener.ListenCrackShot;
import com.SirBlobman.combatlog.listener.ListenTowny;
import com.SirBlobman.combatlog.nms.NMS;
import com.SirBlobman.combatlog.nms.action.Action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
	private static final ConsoleCommandSender CCS = Bukkit.getConsoleSender();
	private static final Server SERVER = Bukkit.getServer();
	protected static final PluginManager PM = SERVER.getPluginManager();
	private static final BukkitScheduler BS = SERVER.getScheduler();
	private static final CombatLogX PLUGIN = CombatLogX.instance;
	private static Action action = null;
	
	public static void enable() {
		Config.loadC();
		Config.loadL();
		if(Config.CHECK_UPDATES) Update.print();
		if(Config.ACTION_BAR) {action = NMS.action();}
		if(PM.isPluginEnabled("CrackShot")) {
			Config.ENABLED_CRACK_SHOT = true;
			regEvents(new ListenCrackShot());
			String v = getVersionMessage("CrackShot");
			print(v);
		}
		
		if(PM.isPluginEnabled("Factions")) {
			Config.ENABLED_FACTIONS = true;
			String v = getVersionMessage("Factions");
			print(v);
		}
		
		if(PM.isPluginEnabled("FactionsUUID")) {
			Config.ENABLED_FACTIONS = true;
			String v = getVersionMessage("FactionsUUID");
			print(v);
		}
		
		if(PM.isPluginEnabled("LegacyFactions")) {
			Config.ENABLED_LEGACY_FACTIONS = true;
			String v = getVersionMessage("LegacyFactions");
			print(v);
		}
		
		if(PM.isPluginEnabled("PlaceholderAPI")) {
			CombatPlaceHolders cp = new CombatPlaceHolders();
			cp.hook();
			String v = getVersionMessage("PlaceholderAPI");
			print(v);
		}
		
		if(PM.isPluginEnabled("TitleManager")) {
			Config.ENABLED_TITLE_MANAGER = true;
			String v = getVersionMessage("TitleManager");
			print(v);
		}
		
		if(PM.isPluginEnabled("Towny")) {
			Config.ENABLED_TOWNY = true;
			regEvents(new ListenTowny());
			String v = getVersionMessage("Towny");
			print(v);
		}
		
		if(PM.isPluginEnabled("WorldGuard")) {
			Config.ENABLED_WORLD_GUARD = true;
			String v = getVersionMessage("WorldGuard");
			print(v);
		}
		
		regEvents(new ListenBukkit());
		timer(new Combat(), 1);
	}
	
	public static String getVersion(String pl) {
		Plugin p = PM.getPlugin(pl);
		if(p != null) {
			PluginDescriptionFile pdf = p.getDescription();
			if(pdf != null) {
				String version = pdf.getVersion();
				return version;
			} else return "";
		} else return "";
	}
	
	public static String getVersionMessage(String pl) {
		String version = getVersion(pl);
		String msg = Util.format("&dSupport for '%1s v%2s' is now enabled!", pl, version);
		return msg;
	}
	
	public static void print(String msg) {
		String prt = color(Config.MSG_PREFIX + msg);
		CCS.sendMessage(prt);
	}
	
	public static void broadcast(String msg) {
		String cast = color(Config.MSG_PREFIX + msg);
		SERVER.broadcastMessage(cast);
	}
	
	public static String color(String o) {
		String c = ChatColor.translateAlternateColorCodes('&', o);
		return c;
	}
	
	public static String strip(String c) {
		String o = ChatColor.stripColor(c);
		return o;
	}
	
	public static String format(String o, Object... os) {
		String f = String.format(o, os);
		String c = color(f);
		return c;
	}
	
	public static String json(String msg) {
		String json = "{\"text\": \""  + msg + "\"}";
		return json;
	}
	
	public static void action(Player p, String msg) {
		if(action != null) {
			action.action(p, msg);
		}
	}
	
	public static void boss(Player p) {
		String nms = NMS.nms();
		if(nms.contains("1_10") || nms.contains("1_11") || nms.contains("1_12")) {
			CustomBoss.boss(p);
		}
	}
	
	public static void regEvents(Listener... ls) {
		for(Listener l : ls) {
			if(l != null) PM.registerEvents(l, PLUGIN);
		}
	}
	
	public static void callEvents(Event... es) {
		for(Event e : es) {
			if(e != null) PM.callEvent(e);
		}
	}
	
	public static void timer(Runnable r, int seconds) {
		int time = 20 * seconds;
		BS.runTaskTimer(PLUGIN, r, 0, time);
	}
	
	@SafeVarargs
	public static <T> List<T> newList(T... ts) {
		List<T> list = new ArrayList<T>();
		for(T t : ts) list.add(t);
		return list;
	}
	
	public static <K, V> Map<K, V> newMap() {
		Map<K, V> map = new HashMap<K, V>();
		return map;
	}
	
	public static void msg(Player p, String msg) {
		msg = Config.MSG_PREFIX + msg;
		msg = color(msg);
		p.sendMessage(msg);
	}
}