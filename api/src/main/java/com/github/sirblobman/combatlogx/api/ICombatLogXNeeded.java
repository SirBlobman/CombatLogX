package com.github.sirblobman.combatlogx.api;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

public interface ICombatLogXNeeded {
    @NotNull ICombatLogX getCombatLogX();

    default @NotNull Logger getLogger() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLogger();
    }
}
