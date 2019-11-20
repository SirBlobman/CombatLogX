package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * PlayerReTagEvent is an event that will be fired when a player's timer is extended
 *
 * @author SirBlobman
 */
public class PlayerReTagEvent extends CustomPlayerEvent {
    private final LivingEntity enemy;
    private final PlayerPreTagEvent.TagType tagType;
    private final PlayerPreTagEvent.TagReason tagReason;
    private long combatEnds;
    public PlayerReTagEvent(Player player, LivingEntity enemy, PlayerPreTagEvent.TagType tagType, PlayerPreTagEvent.TagReason tagReason, long combatEnds) {
        super(player);
        this.enemy = enemy;
        this.tagType = tagType;
        this.tagReason = tagReason;
        this.combatEnds = combatEnds;
    }

    /**
     * @return The enemy that will tag the player or null if an enemy does not exist
     * @see #getPlayer()
     */
    public LivingEntity getEnemy() {
        return this.enemy;
    }

    /**
     * @return The type of entity that will cause this player to be tagged
     * @see #getPlayer()
     */
    public PlayerPreTagEvent.TagType getTagType() {
        return this.tagType;
    }

    /**
     * @return The reason that will cause this player to be tagged.
     * @see #getPlayer()
     */
    public PlayerPreTagEvent.TagReason getTagReason() {
        return this.tagReason;
    }

    /**
     * @return The time (in millis) that the combat timer will end.<br/>
     * This can change if the player is tagged again
     * @see #getPlayer()
     */
    public long getEndTime() {
        return this.combatEnds;
    }

    /**
     * Set the amount of time to wait before the player escapes from combat
     * The default is {@code System.getCurrentTimeMillis() + 30_000L}
     * @param millis The epoch time (in milliseconds) that the timer will end.
     */
    public void setEndTime(long millis) {
        this.combatEnds = millis;
    }
}