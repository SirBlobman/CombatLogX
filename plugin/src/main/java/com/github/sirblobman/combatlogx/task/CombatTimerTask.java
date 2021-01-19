package com.github.sirblobman.combatlogx.task;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.manager.CombatManager;

public final class CombatTimerTask extends BukkitRunnable {
    private final CombatPlugin plugin;
    public CombatTimerTask(CombatPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void start() {
        runTaskTimerAsynchronously(this.plugin, 5L, 1L);
    }

    @Override
    public void run() {
        CombatManager combatManager = this.plugin.getCombatManager();
        List<Player> playerList = combatManager.getPlayersInCombat();
        for(Player player : playerList) {
            triggerEvent(player);

            long timerLeftMillis = combatManager.getTimerLeftMillis(player);
            if(timerLeftMillis <= 0) untag(player);
        }
    }

    private void triggerEvent(Player player) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        PlayerCombatTimerChangeEvent event = new PlayerCombatTimerChangeEvent(player);
        pluginManager.callEvent(event);
    }

    private void untag(Player player) {
        Runnable task = () -> syncUntag(player);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(this.plugin, task);
    }

    private void syncUntag(Player player) {
        CombatManager combatManager = this.plugin.getCombatManager();
        combatManager.untag(player, UntagReason.EXPIRE);
    }
}