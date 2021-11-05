package com.github.sirblobman.combatlogx.api.manager;

import java.util.Set;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public interface ITimerManager {
    /**
     * @return A {@link Set} of {@link TimerUpdater}s that are currently registerd.
     */
    Set<TimerUpdater> getTimerUpdaters();
    
    /**
     * Register a {@link TimerUpdater} instance.
     *
     * @param task The instance to register.
     */
    void addUpdaterTask(TimerUpdater task);
    
    void remove(Player player);
}
