package com.SirBlobman.combatlogx.utility;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class PluginUtil extends Util {
    public static void regEvents(Listener... ll) {
        for (Listener l : ll) {
            if (l != null) PM.registerEvents(l, PLUGIN);
        }
    }

    static void call(Event... ee) {
        for (Event e : ee) {
            if (e != null) PM.callEvent(e);
        }
    }

    public static boolean isEnabled(String plugin) {
        return PM.isPluginEnabled(plugin);
    }

    public static boolean isEnabled(String plugin, String author) {
        if (isEnabled(plugin)) {
            Plugin pl = PM.getPlugin(plugin);
            PluginDescriptionFile pdf = pl.getDescription();
            List<String> authors = pdf.getAuthors();
            return authors.contains(author);
        }

        return false;
    }
    
    public static boolean isEnabled(String plugin, String author, String version) {
        if(isEnabled(plugin, author)) {
            Plugin pl = PM.getPlugin(plugin);
            PluginDescriptionFile pdf = pl.getDescription();
            String plVersion = pdf.getVersion();
            return plVersion.startsWith(version);
        }
        
        return false;
    }
}