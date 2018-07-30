package com.SirBlobman.combatlogx.utility;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class PluginUtil extends Util {
    public static boolean isPluginEnabled(String name) {
        boolean en = PM.isPluginEnabled(name);
        return en;
    }

    public static boolean isPluginEnabled(String name, String author) {
        boolean en = isPluginEnabled(name);
        if (en) {
            Plugin pl = PM.getPlugin(name);
            PluginDescriptionFile pdf = pl.getDescription();
            List<String> authors = pdf.getAuthors();

            return authors.contains(author);
        } else return false;
    }
}