package com.SirBlobman.combatlogx.utility;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PluginUtil extends Util {
	public static void regEvents(Listener... ll) {
		for(Listener l : ll) {if(l != null) PM.registerEvents(l, PLUGIN);}
	}
	
	public static void call(Event... ee) {
		for(Event e : ee) {if(e != null) PM.callEvent(e);}
	}
	
	public static boolean isEnabled(String plugin) {
		return PM.isPluginEnabled(plugin);
	}
	
	public static boolean isEnabled(String plugin, String author) {
		boolean enabled = isEnabled(plugin);
		if(enabled) {
			Plugin pl = PM.getPlugin(plugin);
			PluginDescriptionFile pdf = pl.getDescription();
			List<String> authors = pdf.getAuthors();
			enabled = authors.contains(author);
		}
		
		return enabled;
	}
}