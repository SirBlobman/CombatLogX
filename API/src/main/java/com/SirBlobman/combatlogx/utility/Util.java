package com.SirBlobman.combatlogx.utility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;

public class Util {
    public static final CombatLogX PLUGIN = CombatLogX.INSTANCE;
    public static final Server SERVER = Bukkit.getServer();
    public static final ConsoleCommandSender CONSOLE = SERVER.getConsoleSender();
    public static final PluginManager PM = SERVER.getPluginManager();
    static final BukkitScheduler BS = SERVER.getScheduler();
    
    public static String toString(Object object) {
    	if(object == null) return "";
    	if(object instanceof String) return (String) object;
    	
    	Class<?> object_class = object.getClass();
    	try {
    		Method method = object_class.getMethod("name");
    		return (String) method.invoke(object);
    	} catch(ReflectiveOperationException ex1) {
    		try {
        		Method method = object_class.getMethod("getName");
        		return (String) method.invoke(object);
    		} catch(ReflectiveOperationException ex2) {
    			return object.toString();
    		}
    	}
    }
    
    public static String color(Object object) {
    	String string = toString(object);
    	return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    public static String[] color(Object... objects) {
    	String[] colorArray = new String[objects.length];
    	for(int i = 0; i < objects.length; i++) {
    		Object object = objects[i];
    		String string = color(object);
    		colorArray[i] = string;
    	}
    	return colorArray;
    }
    
    public static String removeColor(Object object) {
    	String string = toString(object);
    	return ChatColor.stripColor(string);
    }
    
    public static String formatMessage(Object object, List<String> keyList, List<?> valueList, Object... objects) {
    	int keyListSize = keyList.size();
    	int valueListSize = valueList.size();
    	if(keyListSize != valueListSize) {
    		String error = "You must have the same number of keys and values!";
    		throw new IllegalArgumentException(error);
    	}
    	
    	String string = toString(object);
    	for(int i = 0; i < keyListSize; i++) {
    		String key = keyList.get(i);
    		Object value = valueList.get(i);
    		String stringValue = toString(value);
    		string = string.replace(key, stringValue);
    	}
    	
    	String format = String.format(string, objects);
    	return color(format);
    }
    
    public static void debug(String... messages) {
    	if(!ConfigOptions.OPTION_DEBUG) return;
    	
    	Logger logger = PLUGIN.getLogger();
    	for(String message : messages) {
    		message = message.replace('\u00A7', '&');
    		logger.info("[Debug] " + message);
    	}
    }
    
    /**
     * Console messages with prefix and no color
     * @param objects All objects to be converted into strings and sent to console
     */
    public static void log(Object... objects) {
    	if(objects.length == 1 && toString(objects[0]).isEmpty()) return;
    	
    	for(Object object : objects) {
    		String string = removeColor(object);
    		String prefix = removeColor(ConfigLang.get("messages.plugin prefix"));
    		CONSOLE.sendMessage(prefix + " " + string);
    	}
    }

    /**
     * Console messages with prefix and color
     * @param objects All objects to be converted into strings and sent to console
     */
    public static void print(Object... objects) {
    	if(objects.length == 1 && toString(objects[0]).isEmpty()) return;
    	
    	for(Object object : objects) {
    		String string = color(object);
    		String prefix = ConfigLang.get("messages.plugin prefix");
    		printNoPrefix(prefix + " " + string);
    	}
    }

    /**
     * Console messages with no prefix and color
     * @param objects All objects to be converted into strings and sent to console
     */
    public static void printNoPrefix(Object... objects) {
    	if(objects.length == 1 && toString(objects[0]).isEmpty()) return;
    	
    	for(Object object : objects) {
    		String string = color(object);
    		CONSOLE.sendMessage(string);
    	}
    }
    
    public static void broadcast(boolean prefix, Object... objects) {
    	for(Object object : objects) {
    		String string = color(object);
    		if(string.isEmpty()) continue;
    		
    		String prefixString = ConfigLang.get("messages.plugin prefix") + " ";
    		SERVER.broadcastMessage((prefix ? prefixString : "") + string);
    	}
    }

    /**
     * Send a list of messages to a {@link CommandSender}<br/>
     * If the message is empty or null it won't be sent
     *
     * @param sender The {@link CommandSender} that will receive the message
     * @param objects A list of objects which will be converted to strings using {@link Util#toString(Object)}
     */
    public static void sendMessage(CommandSender sender, Object... objects) {
    	for(Object object : objects) {
    		String string = color(object);
    		if(string.isEmpty()) continue;
    		
    		sender.sendMessage(string);
    	}
    }

    @SafeVarargs
    public static <L> List<L> newList(L... ll) {
    	return com.SirBlobman.api.utility.Util.newList(ll);
    }

    public static <L> List<L> newList(Collection<L> ll) {
    	return com.SirBlobman.api.utility.Util.newList(ll);
    }

    public static List<String> toLowercaseList(Collection<String> original) {
        List<String> list = newList();
        for(String string : original) {
        	String lower = string.toLowerCase();
        	list.add(lower);
        }
        return list;
    }

    public static <K, V> Map<K, V> newMap() {
    	return com.SirBlobman.api.utility.Util.newMap();
    }

    static <K, V> Map<K, V> newMap(Map<K, V> kv) {
    	return com.SirBlobman.api.utility.Util.newMap(kv);
    }

    public static List<String> getMatching(List<String> original, String arg) {
    	List<String> list = newList();
    	for(String string : original) {
    		if(!string.startsWith(arg) && !string.equals(arg)) continue;
    		list.add(string);
    	}
    	return list;
    }
    
    public static Vector getVector(Location fromLoc, Location toLoc) {
    	if(fromLoc == null || toLoc == null) return null;
    	
    	Vector fromVec = fromLoc.toVector();
    	Vector toVec = toLoc.toVector();
    	
    	Vector subtract = fromVec.subtract(toVec);
    	Vector normal = subtract.normalize();
    	return makeFinite(normal);
    }
    
    public static Vector makeFinite(Vector original) {
    	double x = makeFinite(original.getX());
    	double y = makeFinite(original.getY());
    	double z = makeFinite(original.getZ());
    	
    	return new Vector(x, y, z);
    }
    
    public static double makeFinite(double number) {
    	if(Double.isNaN(number)) return 0.0D;
    	if(Double.isInfinite(number)) {
    		boolean negative = (number < 0.0D);
    		return (negative ? -1.0D : 1.0D);
    	}
    	
    	return number;
    }
}