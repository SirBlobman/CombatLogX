package com.SirBlobman.combatlogx.expansion.notifier.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import be.maximvdw.placeholderapi.EventAPI;

public final class MVdWHandler {
    public static void enableTrigger(String pluginName, String trigger, Player player) {
        try {
            PluginManager manager = Bukkit.getPluginManager();
            if(!manager.isPluginEnabled(pluginName)) return;
            Plugin plugin = manager.getPlugin(pluginName);

            if(!manager.isPluginEnabled("MVdWPlaceholderAPI")) return;
            EventAPI.triggerEvent(plugin, player, trigger, true);
        } catch(Exception ignored) {}
    }

    public static void disableTrigger(String pluginName, String trigger, Player player) {
        try {
            PluginManager manager = Bukkit.getPluginManager();
            if(!manager.isPluginEnabled(pluginName)) return;
            Plugin plugin = manager.getPlugin(pluginName);

            if(!manager.isPluginEnabled("MVdWPlaceholderAPI")) return;
            EventAPI.triggerEvent(plugin, player, trigger, false);
        } catch(Exception ignored) {}
    }
}