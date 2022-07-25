package com.github.sirblobman.combatlogx.api.listener;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CombatListener implements Listener {
    private final ICombatLogX plugin;

    public CombatListener(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void register() {
        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    protected final ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final JavaPlugin getJavaPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    protected final Logger getPluginLogger() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getLogger();
    }

    protected final ConfigurationManager getPluginConfigurationManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getConfigurationManager();
    }

    protected final LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    protected final PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    protected final ICombatManager getCombatManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getCombatManager();
    }

    protected final IDeathManager getDeathManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getDeathManager();
    }

    protected final boolean isInCombat(Player player) {
        ICombatManager combatManager = getCombatManager();
        return combatManager.isInCombat(player);
    }

    protected final String getMessageWithPrefix(@Nullable CommandSender sender, @NotNull String key,
                                                @Nullable Replacer replacer, boolean color) {
        ICombatLogX plugin = getCombatLogX();
        LanguageManager languageManager = plugin.getLanguageManager();

        String message = languageManager.getMessage(sender, key, replacer, color);
        if (message.isEmpty()) {
            return "";
        }

        String prefix = languageManager.getMessage(sender, "prefix", null, true);
        return (prefix.isEmpty() ? message : String.format(Locale.US, "%s %s", prefix, message));
    }

    protected final void sendMessageWithPrefix(@NotNull CommandSender sender, @NotNull String key,
                                               @Nullable Replacer replacer, boolean color) {
        String message = getMessageWithPrefix(sender, key, replacer, color);
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }

    protected final boolean isDebugModeDisabled() {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("debug-mode", false);
    }

    protected void printDebug(String message) {
        if (isDebugModeDisabled()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger pluginLogger = getPluginLogger();
        pluginLogger.info(logMessage);
    }

    protected final boolean isWorldDisabled(Entity entity) {
        Location location = entity.getLocation();
        return isWorldDisabled(location);
    }

    protected final boolean isWorldDisabled(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return true;
        }

        return isWorldDisabled(world);
    }

    protected final boolean isWorldDisabled(World world) {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        List<String> disabledWorldList = configuration.getStringList("disabled-world-list");
        boolean inverted = configuration.getBoolean("disabled-world-list-inverted");

        String worldName = world.getName();
        boolean contains = disabledWorldList.contains(worldName);
        return (inverted != contains);
    }

    @Nullable
    protected final Entity getCurrentEnemy(ICombatLogX plugin, Player player) {
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return null;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        if (enemyList.isEmpty()) {
            return null;
        }

        return enemyList.get(0);
    }
}
