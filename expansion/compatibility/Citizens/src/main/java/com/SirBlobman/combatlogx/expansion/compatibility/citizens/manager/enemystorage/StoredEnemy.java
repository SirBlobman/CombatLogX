package com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.enemystorage;

import java.util.UUID;

public class StoredEnemy {

    private final UUID enemyUUID;
    private final StoredEnemy.EnemyType enemyType;

    public StoredEnemy(final UUID enemyUUID, final StoredEnemy.EnemyType enemyType) {
        this.enemyUUID = enemyUUID;
        this.enemyType = enemyType;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public UUID getEnemyUUID() {
        return enemyUUID;
    }

    public enum EnemyType {
        PLAYER,
        MOB
    }

}
