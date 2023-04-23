package com.github.sirblobman.combatlogx.manager;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

public abstract class Manager implements ICombatLogXNeeded {
    private final ICombatLogX plugin;

    public Manager(@NotNull ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public final @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final @NotNull PlayerDataManager getPlayerDataManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getPlayerDataManager();
    }

    protected final void printDebug(String @NotNull ... messages) {
        ICombatLogX plugin = getCombatLogX();
        plugin.printDebug(messages);
    }
}
