package com.github.sirblobman.combatlogx.api.object;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;

public final class CombatTag implements Comparable<CombatTag> {
    private final UUID enemyId;
    private final WeakReference<Entity> enemyReference;
    private final TagType tagType;
    private final TagReason tagReason;
    private final long expireMillis;

    public CombatTag(@Nullable Entity enemy, @NotNull TagType tagType, @NotNull TagReason tagReason,
                     long expireMillis) {
        if (enemy != null) {
            this.enemyId = enemy.getUniqueId();
            this.enemyReference = new WeakReference<>(enemy);
        } else {
            this.enemyId = null;
            this.enemyReference = null;
        }

        this.tagType = tagType;
        this.tagReason = tagReason;
        this.expireMillis = expireMillis;
    }

    public @Nullable UUID getEnemyId() {
        return this.enemyId;
    }

    public @Nullable Entity getEnemy() {
        if (this.enemyReference == null) {
            return null;
        }

        return this.enemyReference.get();
    }

    public boolean doesEnemyMatch(@NotNull Entity entity) {
        Entity enemy = getEnemy();
        return (entity == enemy);
    }

    public @NotNull TagType getTagType() {
        return this.tagType;
    }

    public @NotNull TagReason getTagReason() {
        return this.tagReason;
    }

    public long getExpireMillis() {
        return this.expireMillis;
    }

    public boolean isExpired() {
        long systemMillis = System.currentTimeMillis();
        long expireMillis = getExpireMillis();
        return (systemMillis >= expireMillis);
    }

    @Override
    public int compareTo(@NotNull CombatTag other) {
        long thisExpireMillis = getExpireMillis();
        long otherExpireMillis = other.getExpireMillis();
        return Long.compare(thisExpireMillis, otherExpireMillis);
    }
}
