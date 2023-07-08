package com.github.sirblobman.combatlogx.api.command;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public abstract class CombatLogCommand extends Command {
    private final ICombatLogX plugin;

    public CombatLogCommand(@NotNull ICombatLogX plugin, @NotNull String commandName) {
        super(plugin.getPlugin(), commandName);
        this.plugin = plugin;
    }

    protected final @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @Override
    protected final @NotNull LanguageManager getLanguageManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getLanguageManager();
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        return false;
    }

    protected final @NotNull ExpansionManager getExpansionManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getExpansionManager();
    }

    protected final boolean isWorldDisabled(World world) {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();
        return configuration.isDisabled(world);
    }
}
