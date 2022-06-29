package com.github.sirblobman.combatlogx.task;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

/**
 * This task is used to untag players from combat. It runs every 10 ticks.
 */
public final class UntagTask implements Runnable {
    private final ICombatLogX plugin;

    public UntagTask(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    public void register() {
        JavaPlugin plugin = this.plugin.getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, this, 5L, 10L);
    }

    @Override
    public void run() {
        ICombatManager combatManager = this.plugin.getCombatManager();
        List<Player> playerCombatList = combatManager.getPlayersInCombat();
        for (Player player : playerCombatList) {
            long timeLeftMillis = combatManager.getTimerLeftMillis(player);
            if (timeLeftMillis > 0) {
                continue;
            }

            combatManager.untag(player, UntagReason.EXPIRE);
        }
    }
}
