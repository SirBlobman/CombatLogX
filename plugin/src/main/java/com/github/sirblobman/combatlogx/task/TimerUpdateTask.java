package com.github.sirblobman.combatlogx.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.TaskDetails;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class TimerUpdateTask extends TaskDetails implements ITimerManager {
    private final ICombatLogX plugin;
    private final Set<TimerUpdater> timerUpdaterSet;

    public TimerUpdateTask(@NotNull ICombatLogX plugin) {
        super(plugin.getPlugin());
        this.plugin = plugin;
        this.timerUpdaterSet = new HashSet<>();
    }

    @Override
    public @NotNull ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    @Override
    public @NotNull Set<TimerUpdater> getTimerUpdaters() {
        return Collections.unmodifiableSet(this.timerUpdaterSet);
    }

    @Override
    public void addUpdaterTask(@NotNull TimerUpdater task) {
        this.timerUpdaterSet.add(task);
    }

    @Override
    public void run() {
        ICombatManager combatManager = this.plugin.getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        playerCombatList.forEach(this::update);
    }

    @Override
    public void remove(@NotNull Player player) {
        Set<TimerUpdater> timerUpdaterSet = getTimerUpdaters();
        for (TimerUpdater timerUpdater : timerUpdaterSet) {
            timerUpdater.remove(player);
        }
    }

    public void register() {
        ICombatLogX plugin = getCombatLogX();
        TaskScheduler scheduler = plugin.getFoliaHelper().getScheduler();

        setDelay(5L);
        setPeriod(10L);
        scheduler.scheduleTask(this);
    }

    private void update(@NotNull Player player) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        if (tagInformation.isExpired()) {
            return;
        }

        long timeLeftMillis = tagInformation.getMillisLeftCombined();
        Set<TimerUpdater> timerUpdaterSet = getTimerUpdaters();
        for (TimerUpdater timerUpdater : timerUpdaterSet) {
            timerUpdater.update(player, timeLeftMillis);
        }
    }
}
