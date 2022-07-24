package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

/**
 * A custom event that will be fired when a player's timer is extended
 *
 * @author SirBlobman
 */
public final class PlayerReTagEvent extends CustomPlayerEventCancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private final Entity enemy;
    private final TagType tagType;
    private final TagReason tagReason;
    private long combatEndMillis;

    public PlayerReTagEvent(Player player, Entity enemy, TagType tagType, TagReason tagReason, long combatEndMillis) {
        super(player);
        this.enemy = enemy;
        this.tagType = Validate.notNull(tagType, "tagType must not be null!");
        this.tagReason = Validate.notNull(tagReason, "tagReason must not be null!");
        this.combatEndMillis = combatEndMillis;
    }

    /**
     * @return The enemy that will tag the player or null if an enemy does not exist
     * @see #getPlayer()
     */
    public Entity getEnemy() {
        return this.enemy;
    }

    /**
     * @return The type of entity that will cause this player to be tagged
     * @see #getPlayer()
     */
    public TagType getTagType() {
        return this.tagType;
    }

    /**
     * @return The reason that will cause this player to be tagged.
     * @see #getPlayer()
     */
    public TagReason getTagReason() {
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
     * Set the amount of time to wait before the player escapes from combat The default is {@code
     * System.getCurrentTimeMillis() + 30_000L}
     *
     * @param millis The epoch time (in milliseconds) that the timer will end.
     */
    public void setEndTime(long millis) {
        this.combatEndMillis = millis;
    }
}
