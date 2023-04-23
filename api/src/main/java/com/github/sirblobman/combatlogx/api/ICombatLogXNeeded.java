package com.github.sirblobman.combatlogx.api;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.plugin.java.JavaPlugin;

public interface ICombatLogXNeeded {
    @NotNull ICombatLogX getCombatLogX();

    default @NotNull JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    default @NotNull Logger getLogger() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLogger();
    }
}
