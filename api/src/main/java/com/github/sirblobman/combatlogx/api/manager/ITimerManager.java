package com.github.sirblobman.combatlogx.api.manager;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public interface ITimerManager extends ICombatLogXNeeded {
    /**
     * @return A {@link Set} of {@link TimerUpdater}s that are currently registered.
     */
    @NotNull Set<TimerUpdater> getTimerUpdaters();

    /**
     * Register a {@link TimerUpdater} instance.
     *
     * @param task The instance to register.
     */
    void addUpdaterTask(@NotNull TimerUpdater task);

    /**
     * Remove all timers in this manager from the player.
     *
     * @param player The {@link Player} to remove the timers from.
     */
    void remove(@NotNull Player player);

    /**
     * Register the manager
     */
    void register();
}
