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

    public DeathManager(ICombatLogX plugin) {
        super(plugin);
        this.killedPlayerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void kill(Player player, List<Entity> enemyList) {
        UUID playerId = player.getUniqueId();
        this.killedPlayerMap.put(playerId, enemyList);
        player.setHealth(0.0D);
    }

    @Override
    public boolean wasPunishKilled(Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerMap.containsKey(playerId);
    }

    @Override
    public boolean stopTracking(Player player) {
        UUID playerId = player.getUniqueId();
        boolean contained = this.killedPlayerMap.containsKey(playerId);
        this.killedPlayerMap.remove(playerId);
        return contained;
    }

    @NotNull
    @Override
    public List<Entity> getTrackedEnemies(Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerMap.getOrDefault(playerId, Collections.emptyList());
    }
}
