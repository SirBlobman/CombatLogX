package com.github.sirblobman.combatlogx.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

public abstract class CombatListener implements Listener {
    private final ICombatLogX plugin;
    public CombatListener(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void register() {
        ICombatLogX combatLogX = getPlugin();
        JavaPlugin plugin = combatLogX.getPlugin();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    protected final ICombatLogX getPlugin() {
        return this.plugin;
    }

    protected final ConfigurationManager getConfigurationManager() {
        ICombatLogX plugin = getPlugin();
        return plugin.getConfigurationManager();
    }

    protected final ICombatManager getCombatManager() {
        ICombatLogX plugin = getPlugin();
        return plugin.getCombatManager();
    }
}
