package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

import org.jetbrains.annotations.NotNull;

public interface IDeathManager extends ICombatLogXNeeded {
    /**
     * Track and kill a player.
     * The player will be killed by setting their health to zero.
     * @param player The {@link Player} to kill.
     */
    void kill(Player player, List<Entity> enemyList);

    /**
     * Check if a player was killed while tracked.
     * @param player The {@link Player} to check.
     * @return {@code true} if the player died from CombatLogX,
     * {@code false} if they were killed by any other reason.
     */
    boolean wasPunishKilled(Player player);

    /**
     * Stop tracking a player.
     * @param player The {@link Player} to stop tracking.
     * @return {@code true} if the player was previously being tracked.
     */
    boolean stopTracking(Player player);

    @NotNull List<Entity> getTrackedEnemies(Player player);
}
