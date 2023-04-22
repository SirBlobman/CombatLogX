package com.github.sirblobman.combatlogx.manager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

import org.jetbrains.annotations.NotNull;

public final class DeathManager extends Manager implements IDeathManager {
    private final Map<UUID, List<Entity>> killedPlayerMap;

    public DeathManager(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.killedPlayerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void kill(@NotNull Player player, @NotNull List<Entity> enemyList) {
        UUID playerId = player.getUniqueId();
        this.killedPlayerMap.put(playerId, enemyList);
        player.setHealth(0.0D);
    }

    @Override
    public boolean wasPunishKilled(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerMap.containsKey(playerId);
    }

    @Override
    public boolean stopTracking(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        List<Entity> oldValue = this.killedPlayerMap.remove(playerId);
        return (oldValue != null);
    }

    @Override
    public @NotNull List<Entity> getTrackedEnemies(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerMap.getOrDefault(playerId, Collections.emptyList());
    }
}
