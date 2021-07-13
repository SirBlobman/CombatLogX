package com.github.sirblobman.combatlogx.api;

import java.util.Set;

import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public interface ITimerManager {
    /**
     * @return A {@link Set} of {@link TimerUpdater}s that are currently registerd.
     */
    Set<TimerUpdater> getTimerUpdaters();

    /**
     * Register a {@link TimerUpdater} instance.
     * @param task The instance to register.
     */
    void addUpdaterTask(TimerUpdater task);
}
