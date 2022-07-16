package com.github.sirblobman.combatlogx.api.object;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private final Map<UUID, WeakReference<Entity>> entityMap;
    private long noEnemyExpireMillis;

    public TagInformation(OfflinePlayer player) {
        Validate.notNull(player, "player must not be null!");
        this.playerId = player.getUniqueId();
        this.enemyMap = new ConcurrentHashMap<>();
        this.entityMap = new ConcurrentHashMap<>();
        this.noEnemyExpireMillis = 0L;
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

    public Set<UUID> getEnemyIds() {
        Set<UUID> enemyIdSet = this.enemyMap.keySet();
        return Collections.unmodifiableSet(enemyIdSet);
    }

    public List<Entity> getEnemies() {
        Set<UUID> enemyIdSet = getEnemyIds();
        List<Entity> enemyList = new ArrayList<>();

        for (UUID enemyId : enemyIdSet) {
            WeakReference<Entity> weakReference = this.entityMap.get(enemyId);
            if(weakReference == null) {
                continue;
            }

            Entity entity = weakReference.get();
            if(entity == null) {
                this.entityMap.remove(enemyId);
                continue;
            }

            enemyList.add(entity);
        }

        return Collections.unmodifiableList(enemyList);
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

        WeakReference<Entity> weakReference = new WeakReference<>(entity);
        this.entityMap.put(entityId, weakReference);
    }

    public void addNoEnemy(long expireMillis) {
        long systemMillis = System.currentTimeMillis();
        if(systemMillis >= expireMillis) {
            throw new IllegalArgumentException("expireMillis is already expired!");
        }

        this.noEnemyExpireMillis = expireMillis;
    }

    public void removeEnemy(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        UUID entityId = entity.getUniqueId();
        this.enemyMap.remove(entityId);
        this.entityMap.remove(entityId);
    }

    public long getNoEnemyExpireMillis() {
        return this.noEnemyExpireMillis;
    }

    public long getExpireMillis(Entity entity) {
        Validate.notNull(entity, "entity must not be null!");

        UUID entityId = entity.getUniqueId();
        return this.enemyMap.getOrDefault(entityId, 0L);
    }

    public long getExpireMillisCombined() {
        long expireMillis = getNoEnemyExpireMillis();

        Collection<Long> valueCollection = this.enemyMap.values();
        for (Long value : valueCollection) {
            expireMillis = Math.max(expireMillis, value);
        }

        return expireMillis;
    }
}
