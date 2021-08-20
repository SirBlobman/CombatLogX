package com.github.sirblobman.combatlogx.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class TimerUpdateTask implements ITimerManager, Runnable {
    private final ICombatLogX plugin;
    private final Set<TimerUpdater> timerUpdaterSet;

    public TimerUpdateTask(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.timerUpdaterSet = new HashSet<>();
    }

    public void register() {
        JavaPlugin plugin = this.plugin.getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, this, 5L, 10L);
    }

    @Override
    public Set<TimerUpdater> getTimerUpdaters() {
        return Collections.unmodifiableSet(this.timerUpdaterSet);
    }

    @Override
    public void addUpdaterTask(TimerUpdater task) {
        this.timerUpdaterSet.add(task);
    }

    @Override
    public void run() {
        ICombatManager combatManager = this.plugin.getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        playerCombatList.forEach(this::update);
    }
    
    @Override
    public void remove(Player player) {
        Set<TimerUpdater> timerUpdaterSet = getTimerUpdaters();
        for(TimerUpdater timerUpdater : timerUpdaterSet) {
            timerUpdater.remove(player);
        }
    }
    
    private void update(Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        long timeLeftMillis = combatManager.getTimerLeftMillis(player);
        if(timeLeftMillis <= 0L) {
            return;
        }
    
        Set<TimerUpdater> timerUpdaterSet = getTimerUpdaters();
        for(TimerUpdater timerUpdater : timerUpdaterSet) {
            timerUpdater.update(player, timeLeftMillis);
        }
    }
}
