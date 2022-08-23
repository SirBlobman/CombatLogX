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

import com.github.sirblobman.api.adventure.adventure.audience.Audience;
import com.github.sirblobman.api.adventure.adventure.platform.bukkit.BukkitAudiences;
import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextComponent.Builder;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

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

    @NotNull
    protected final ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @NotNull
    protected final JavaPlugin getJavaPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    @NotNull
    protected final Logger getPluginLogger() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getLogger();
    }

    @NotNull
    protected final ConfigurationManager getPluginConfigurationManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getConfigurationManager();
    }

    @NotNull
    protected final LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    @NotNull
    protected final PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    @NotNull
    protected final ICombatManager getCombatManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getCombatManager();
    }

    @NotNull
    protected final IDeathManager getDeathManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getDeathManager();
    }

    protected final boolean isInCombat(Player player) {
        ICombatManager combatManager = getCombatManager();
        return combatManager.isInCombat(player);
    }

    @NotNull
    protected final Component getMessageWithPrefix(@Nullable CommandSender audience, @NotNull String key,
                                                   @Nullable Replacer replacer) {
        LanguageManager languageManager = getLanguageManager();
        Component message = languageManager.getMessage(audience, key, replacer);
        if (Component.empty().equals(message)) {
            return Component.empty();
        }

        Component prefix = languageManager.getMessage(audience, "prefix", null);
        if (Component.empty().equals(prefix)) {
            return message;
        }

        Builder builder = Component.text();
        builder.append(prefix);
        builder.append(Component.space());
        builder.append(message);
        return builder.build();
    }

    protected final void sendMessageWithPrefix(@NotNull CommandSender audience, @NotNull String key,
                                               @Nullable Replacer replacer) {
        Component message = getMessageWithPrefix(audience, key, replacer);
        if (Component.empty().equals(message)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        BukkitAudiences audiences = languageManager.getAudiences();
        if (audiences == null) {
            return;
        }

        Audience realAudience = audiences.sender(audience);
        realAudience.sendMessage(message);
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
}
