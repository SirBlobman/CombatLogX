package com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.enemystorage;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.enemystorage.map.SelfExpiringHashMap;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.enemystorage.map.SelfExpiringMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

/**
 *
 * Storage for when players are offline.
 *
 */
public class EnemyStorageManager {

    private static final long DEFAULT_EXPIRE = 30*1000;

    private final SelfExpiringMap<UUID, StoredEnemy> enemy_storage = new SelfExpiringHashMap<>(DEFAULT_EXPIRE);

    public void store(final UUID playerUUID, final LivingEntity enemy, final long millisleft) {
        if(enemy == null) return;
        this.enemy_storage.put(playerUUID, new StoredEnemy(enemy.getUniqueId(), enemy.getType() == EntityType.PLAYER ? StoredEnemy.EnemyType.PLAYER : StoredEnemy.EnemyType.MOB), millisleft);
    }

    public void store(final UUID playerUUID, final LivingEntity enemy) {
        this.store(playerUUID, enemy, DEFAULT_EXPIRE);
    }

    public void store(final  UUID playerUUID, final StoredEnemy storedEnemy) {
        this.enemy_storage.put(playerUUID, storedEnemy);
    }

    public StoredEnemy get(final UUID playerUUID) {
        return this.enemy_storage.get(playerUUID);
    }

    public void remove(final UUID playerUUID) {
        this.enemy_storage.remove(playerUUID);
    }

}