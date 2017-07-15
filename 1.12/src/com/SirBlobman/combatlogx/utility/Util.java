package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.Update;
import com.SirBlobman.combatlogx.compat.CombatPlaceHolders;
import com.SirBlobman.combatlogx.compat.CompatTowny;
import com.SirBlobman.combatlogx.compat.CustomBoss;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.expand.Expansions;
import com.SirBlobman.combatlogx.listener.ListenBukkit;
import com.SirBlobman.combatlogx.listener.ListenCrackShot;
import com.SirBlobman.combatlogx.nms.NMS;
import com.SirBlobman.combatlogx.nms.action.Action;

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
		if(Config.OPTION_CHECK_UPDATES) Update.print();
		if(Config.OPTION_ACTION_BAR) {
			action = NMS.action();
			if(action == null) {
				String error = "ActionBar failed to initialize, check your NMS version";
				print(error);
				Config.OPTION_ACTION_BAR = false;
			}
		}
		if(PM.isPluginEnabled("CrackShot")) {
			Config.ENABLED_CRACK_SHOT = true;
			regEvents(new ListenCrackShot());
			String v = getVersionMessage("CrackShot");
			print(v);
		}
		
		if(PM.isPluginEnabled("Factions")) {
			String version = getVersion("Factions");
			if(version.startsWith("1")) {
				Config.ENABLED_FACTIONS_UUID = true;
				String v = getVersionMessage("FactionsUUID", version);
				print(v);
			} else {
				Config.ENABLED_FACTIONS_NORMAL = true;
				String v = getVersionMessage("Factions", version);
				print(v);
			}
		}
		
		if(PM.isPluginEnabled("LegacyFactions")) {
			Config.ENABLED_FACTIONS_LEGACY = true;
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
			regEvents(new CompatTowny());
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
		Expansions.load();
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
		return getVersionMessage(pl, version);
	}
	
	public static String getVersionMessage(String pl, String vs) {
		String msg = Util.format("&dSupport for '%1s v%2s' is now enabled!", pl, vs);
		return msg;
	}
	
	public static void print(String msg) {
		String prt = color(Config.MESSAGE_PREFIX + msg);
		CCS.sendMessage(prt);
	}
	
	public static void broadcast(String msg) {
		print(msg);
		String cast = color(Config.MESSAGE_PREFIX + msg);
		for(Player p : Bukkit.getOnlinePlayers()) p.sendMessage(cast);
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
	
	public static String formatMessage(String o, List<String> keys, List<String> values) {
		if(keys.size() != values.size()) {
			String error = "Invalid key/value set! They must be the same size!";
			IllegalArgumentException ex = new IllegalArgumentException(error);
			throw ex;
		} else {
			for(int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				String val = values.get(i);
				o = o.replace(key, val);
			}
			String c = color(o);
			return c;
		}
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
	public static <L> List<L> newList(L... ll) {
		List<L> list = new ArrayList<L>();
		for(L l : ll) list.add(l);
		return list;
	}
	
	public static <K, V> Map<K, V> newMap() {
		Map<K, V> map = new HashMap<K, V>();
		return map;
	}
	
	public static void msg(Player p, String msg) {
		msg = Config.MESSAGE_PREFIX + msg;
		msg = color(msg);
		p.sendMessage(msg);
	}
}