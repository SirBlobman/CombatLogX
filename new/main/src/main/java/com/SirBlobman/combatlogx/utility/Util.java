package com.SirBlobman.combatlogx.utility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;

import net.md_5.bungee.api.ChatColor;

public class Util {
	public static final CombatLogX PLUGIN = CombatLogX.INSTANCE;
	public static final Server SERVER = Bukkit.getServer();
	public static final ConsoleCommandSender CONSOLE = SERVER.getConsoleSender();
	public static final PluginManager PM = SERVER.getPluginManager();
	public static final BukkitScheduler BS = SERVER.getScheduler();
	public static final ScoreboardManager SM = SERVER.getScoreboardManager();
	
	public static String str(Object o) {
		if(o == null) return "";
		else if(o instanceof String) {
			String str = (String) o;
			return str;
		} else if((o instanceof Byte) || (o instanceof Short) || (o instanceof Integer) || (o instanceof Long)) {
			Number n = (Number) o;
			Long l = n.longValue();
			String str = l.toString();
			return str;
		} else if((o instanceof Float) || (o instanceof Double) || (o instanceof Number)) {
			Number n = (Number) o;
			Double d = n.doubleValue();
			String str = d.toString();
			return str;
		} else {
			Class<?> clazz = o.getClass();
			try {
				Method method = clazz.getMethod("name");
				String str = (String) method.invoke(o);
				return str;
			} catch(Throwable ex1) {
				try {
					Method method = clazz.getMethod("getName");
					String str = (String) method.invoke(o);
					return str;
				} catch(Throwable ex2) {
					String str = o.toString();
					return str;
				}
			}
		}
	}
	
	public static String color(Object o) {
		String str = str(o);
		String color = ChatColor.translateAlternateColorCodes('&', str);
		return color;
	}
	
	public static String strip(Object o) {
		String str = str(o);
		String strip = ChatColor.stripColor(str);
		return strip;
	}
	
	public static String[] color(Object... oo) {
		String[] cc = new String[oo.length];
		int i = 0;
		for(Object o : oo) {
			String str = color(o);
			cc[i] = str;
			i++;
		} return cc;
	}
	
	public static String formatMessage(Object format, List<String> keys, List<?> vals, Object... oo) {
		if(keys.size() != vals.size()) {
            throw new IllegalArgumentException("You must have the same number of keys as you do values!");
        } else {
            String s = str(format);
            for(int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Object val = vals.get(i);
                String sal = str(val);
                s = s.replace(key, sal);
            }
            
            String f = String.format(s, oo);
            return color(f);
        }
	}
	
	public static void log(Object... oo) {
		for(Object o : oo) {
			String prefix = ConfigLang.get("messages.plugin prefix");
			String log = strip(prefix) + " " + strip(o);
			CONSOLE.sendMessage(log);
		}
	}
	
	public static void print(Object... oo) {
		String[] msgs = color(oo);
		for(String msg : msgs) {
			String prefix = ConfigLang.get("messages.plugin prefix");
			String print = prefix + " " + msg;
			printNoPrefix(print);
		}
	}
	
	public static void printNoPrefix(Object... oo) {
		String[] msgs = color(oo);
		for(String msg : msgs) {CONSOLE.sendMessage(msg);}
	}
	
	public static void broadcast(boolean prefix, Object... oo) {
		for(Object o : oo) {
			String str = str(o);
			if(!str.isEmpty()) {
				String sprefix = ConfigLang.get("messages.plugin prefix") + " ";
				String bcast = color((prefix ? sprefix : "") + str);
				SERVER.broadcastMessage(bcast);
			}
		}
	}
	
	public static void sendMessage(CommandSender cs, Object... oo) {
		for(Object o : oo) {
			String str = str(o);
			if(!str.isEmpty()) {
				String msg = color(str);
				cs.sendMessage(msg);
			}
		}
	}
	
	@SafeVarargs
	public static <L> List<L> newList(L... ll) {
		List<L> list = new ArrayList<L>();
		for(L l : ll) list.add(l);
		return list;
	}
	
	public static <L> List<L> newList(Collection<L> ll) {
		List<L> list = new ArrayList<L>();
		ll.forEach(l -> list.add(l));
		return list;
	}
	
	public static List<String> toLowercaseList(List<String> original) {
		List<String> list = newList();
		original.forEach(u -> list.add(u.toLowerCase()));
		return list;
	}
	
	public static <K,V> Map<K, V> newMap() {
		Map<K, V> map = new HashMap<K, V>();
		return map;
	}
	
	public static <K,V> Map<K, V> newMap(Map<K, V> kv) {
		Map<K, V> map = new HashMap<K, V>();
		for(Entry<K, V> e : kv.entrySet()) {
			K key = e.getKey();
			V val = e.getValue();
			map.put(key, val);
		}
		return map;
	}
	
	public static List<String> getMatching(List<String> original, String arg) {
		List<String> list = newList();
		original.forEach(item -> {if(item.startsWith(arg) || item.equals(arg)) list.add(item);});
		return list;
	}
}