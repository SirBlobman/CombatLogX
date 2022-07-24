package com.github.sirblobman.combatlogx.api.object;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.bukkit.entity.Entity;

import com.github.sirblobman.api.utility.Validate;

import org.jetbrains.annotations.Nullable;

public final class CombatTag implements Comparable<CombatTag> {
    private final UUID enemyId;
    private final WeakReference<Entity> enemyReference;
    private final TagType tagType;
    private final TagReason tagReason;
    private final long expireMillis;

    public CombatTag(@Nullable Entity enemy, TagType tagType, TagReason tagReason, long expireMillis) {
        if(enemy != null) {
            this.enemyId = enemy.getUniqueId();
            this.enemyReference = new WeakReference<>(enemy);
        } else {
            this.enemyId = null;
            this.enemyReference = null;
        }

        this.tagType = Validate.notNull(tagType, "tagType must not be null!");
        this.tagReason = Validate.notNull(tagReason, "tagReason must not be null!");
        this.expireMillis = expireMillis;
    }

    @Nullable
    public UUID getEnemyId() {
        return this.enemyId;
    }

    @Nullable
    public Entity getEnemy() {
        if(this.enemyReference == null) {
            return null;
        }

        return this.enemyReference.get();
    }

    public boolean doesEnemyMatch(Entity entity) {
        Entity enemy = getEnemy();
        return (entity == enemy);
    }

    public TagType getTagType() {
        return this.tagType;
    }

    public TagReason getTagReason() {
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
    public int compareTo(CombatTag other) {
        long thisExpireMillis = getExpireMillis();
        long otherExpireMillis = other.getExpireMillis();
        return Long.compare(thisExpireMillis, otherExpireMillis);
    }
}
