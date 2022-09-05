package com.github.sirblobman.combatlogx.api.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import org.jetbrains.annotations.NotNull;

public abstract class CombatLogPlayerCommand extends PlayerCommand {
    private final ICombatLogX plugin;

    public CombatLogPlayerCommand(ICombatLogX plugin, String commandName) {
        super(plugin.getPlugin(), commandName);
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected final LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    protected final ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final ExpansionManager getExpansionManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getExpansionManager();
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
