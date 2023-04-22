package com.github.sirblobman.combatlogx.api.listener;

import java.util.Locale;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

public abstract class CombatListener implements Listener {
    private final ICombatLogX plugin;

    public CombatListener(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
    }

    public void register() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    protected final @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final @NotNull JavaPlugin getJavaPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    protected final @NotNull Logger getPluginLogger() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getLogger();
    }

    protected final @NotNull LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    protected final @NotNull PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    protected final @NotNull ICombatManager getCombatManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getCombatManager();
    }

    protected final @NotNull IDeathManager getDeathManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getDeathManager();
    }

    protected final boolean isInCombat(@NotNull Player player) {
        ICombatManager combatManager = getCombatManager();
        return combatManager.isInCombat(player);
    }

    protected final boolean isDebugModeDisabled() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.isDebugModeDisabled();
    }

    protected void printDebug(@NotNull String message) {
        if (isDebugModeDisabled()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger pluginLogger = getPluginLogger();
        pluginLogger.info(logMessage);
    }

    protected final boolean isWorldDisabled(@NotNull Entity entity) {
        World world = entity.getWorld();
        return isWorldDisabled(world);
    }

    protected final boolean isWorldDisabled(@NotNull World world) {
        ICombatLogX combatLogX = getCombatLogX();
        MainConfiguration configuration = combatLogX.getConfiguration();
        return configuration.isDisabled(world);
    }
}
