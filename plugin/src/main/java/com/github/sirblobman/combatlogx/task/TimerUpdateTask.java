package com.github.sirblobman.combatlogx.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class TimerUpdateTask extends BukkitRunnable implements ITimerManager {
    private final ICombatLogX plugin;
    private final Set<TimerUpdater> timerUpdaterSet;

    public TimerUpdateTask(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.timerUpdaterSet = new HashSet<>();
    }

    public void register() {
        JavaPlugin plugin = this.plugin.getPlugin();
        runTaskTimerAsynchronously(plugin, 5L, 1L);
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
        for(Player player : playerCombatList) {
            long timeLeftMillis = combatManager.getTimerLeftMillis(player);
            if(timeLeftMillis <= 0L) continue;
            update(player, timeLeftMillis);
        }
    }

    private void update(Player player, long timeLeftMillis) {
        for(TimerUpdater task : this.timerUpdaterSet) {
            task.update(player, timeLeftMillis);
        }
    }

    private void remove(Player player) {
        for(TimerUpdater task : this.timerUpdaterSet) {
            task.remove(player);
        }
    }
}
