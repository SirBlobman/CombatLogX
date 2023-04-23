package com.github.sirblobman.combatlogx.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

/**
 * A custom event that will be fired when a player is put into combat.
 * If you want to prevent a player from being tagged, check {@link PlayerPreTagEvent}
 *
 * @author SirBlobman
 */
public final class PlayerTagEvent extends CustomPlayerEvent {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final Entity enemy;
    private final TagType tagType;
    private final TagReason tagReason;
    private long combatEndMillis;

    public PlayerTagEvent(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                          @NotNull TagReason tagReason, long combatEndMillis) {
        super(player);
        this.enemy = enemy;
        this.tagType = tagType;
        this.tagReason = tagReason;
        this.combatEndMillis = combatEndMillis;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * @return The enemy that will tag the player or null if an enemy does not exist
     * @see #getPlayer()
     */
    public @Nullable Entity getEnemy() {
        return this.enemy;
    }

    /**
     * @return The type of entity that will cause this player to be tagged
     * @see #getPlayer()
     */
    public @NotNull TagType getTagType() {
        return this.tagType;
    }

    /**
     * @return The reason that will cause this player to be tagged.
     * @see #getPlayer()
     */
    public @NotNull TagReason getTagReason() {
        return this.tagReason;
    }

    /**
     * @return The time (in millis) that the combat timer will end. This can change if the player is tagged again
     * @see #getPlayer()
     */
    public long getEndTime() {
        return this.combatEndMillis;
    }

    /**
     * Set the amount of time to wait before the player escapes from combat.
     *
     * @param millis The epoch time (in milliseconds) that the timer will end.
     * @see ICombatManager#getMaxTimerSeconds(Player)
     */
    public void setEndTime(long millis) {
        this.combatEndMillis = millis;
    }
}
