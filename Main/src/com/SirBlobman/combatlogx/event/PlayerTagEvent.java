package com.SirBlobman.combatlogx.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This event fires after a player gets put into combat
 *
 * @author SirBlobman
 */
public class PlayerTagEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final LivingEntity enemy;
    private final long combatEnds;
    private final TagReason tagReason;
    private final TagType tagType;
    public PlayerTagEvent(Player p, LivingEntity enemy, TagType type, TagReason reason, long combatEnds) {
        super(p);
        this.enemy = enemy;
        this.tagType = type;
        this.tagReason = reason;
        this.combatEnds = combatEnds;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The enemy that will tag the player (can be null)
     */
    public LivingEntity getEnemy() {
        return enemy;
    }

    /**
     * @return The time (in long format) that the combat will end.<br/>
     * This can change if the player is tagged again
     */
    public long getEndTime() {
        return combatEnds;
    }

    /**
     * @return The type of entity that caused this player to be tagged
     */
    public TagType getTaggedBy() {
        return tagType;
    }

    /**
     * @return The reason this player was tagged
     */
    public TagReason getTagReason() {
        return tagReason;
    }

    public enum TagType {PLAYER, MOB, UNKNOWN}

    public enum TagReason {ATTACKED, ATTACKER, UNKNOWN}
}