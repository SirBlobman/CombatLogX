package com.github.sirblobman.combatlogx.api;

import java.util.Set;

import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public interface ITimerManager {
    Set<TimerUpdater> getTimerUpdaters();
    void addUpdaterTask(TimerUpdater task);
}
