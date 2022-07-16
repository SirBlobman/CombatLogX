package com.github.sirblobman.combatlogx.api.object;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TagInformation {
    private final UUID playerId;
    private final Map<UUID, Long> enemyMap;

    public TagInformation(OfflinePlayer player) {
        Validate.notNull(player, "player must not be null!");
        this.playerId = player.getUniqueId();
        this.enemyMap = new ConcurrentHashMap<>();
    }

    @NotNull
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Nullable
    public OfflinePlayer getOfflinePlayer() {
        UUID playerId = getPlayerId();
        return Bukkit.getOfflinePlayer(playerId);
    }

    @Nullable
    public Player getPlayer() {
        UUID playerId = getPlayerId();
        return Bukkit.getPlayer(playerId);
    }

    public Set<UUID> getEnemies() {
        Set<UUID> enemyIdSet = this.enemyMap.keySet();
        return Collections.unmodifiableSet(enemyIdSet);
    }

    public boolean isEnemy(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        UUID entityId = entity.getUniqueId();
        return this.enemyMap.containsKey(entityId);
    }

    public void addEnemy(Entity entity, long expireMillis) {
        Validate.notNull(entity, "entity must not be null!");

        long systemMillis = System.currentTimeMillis();
        if(systemMillis >= expireMillis) {
            throw new IllegalArgumentException("expireMillis is already expired!");
        }

        UUID entityId = entity.getUniqueId();
        this.enemyMap.put(entityId, expireMillis);
    }

    public void removeEnemy(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        UUID entityId = entity.getUniqueId();
        this.enemyMap.remove(entityId);
    }
}
