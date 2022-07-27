package com.github.sirblobman.combatlogx.api;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public interface ICombatLogXNeeded {
    ICombatLogX getCombatLogX();

    default JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    default Logger getLogger() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLogger();
    }
}
