package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * PlayerPreTagEvent is an event that will be fired before a player is put into combat.
 * If the event is cancelled, the player will not be tagged.
 *
 * @author SirBlobman
 */
public class PlayerPreTagEvent extends CancellableCustomPlayerEvent {
    public enum TagType {PLAYER, MOB, UNKNOWN}
    public enum TagReason {ATTACKED, ATTACKER, UNKNOWN}

    private final LivingEntity enemy;
    private final TagType tagType;
    private final TagReason tagReason;
    public PlayerPreTagEvent(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason) {
        super(player);
        this.enemy = enemy;
        this.tagType = tagType;
        this.tagReason = tagReason;
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
}