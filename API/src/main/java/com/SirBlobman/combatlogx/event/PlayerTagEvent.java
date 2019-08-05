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
    public enum TagType {PLAYER, MOB, UNKNOWN}
    public enum TagReason {ATTACKED, ATTACKER, UNKNOWN}
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    public static HandlerList getHandlerList() {return HANDLER_LIST;}
    public HandlerList getHandlers() {return HANDLER_LIST;}
    
    private final LivingEntity enemy;
    private long combatEnds;
    private final TagReason tagReason;
    private final TagType tagType;
    public PlayerTagEvent(Player player, LivingEntity enemy, TagType type, TagReason reason, long combatEnds) {
        super(player);
        this.enemy = enemy;
        this.tagType = type;
        this.tagReason = reason;
        this.combatEnds = combatEnds;
    }

    /**
     * @return The enemy that will tag the player (can be null)
     */
    public LivingEntity getEnemy() {
        return this.enemy;
    }

    /**
     * @return The time (in long format) that the combat will end.<br/>
     * This can change if the player is tagged again
     */
    public long getEndTime() {
        return this.combatEnds;
    }
    
    /**
     * Set the amount of time to wait before the player escapes from combat
     * The default is {@code System.getCurrentTimeMillis() + (ConfigOptions.OPTION_TIMER * 1000L);}
     * @param systemTime The system time (in milliseconds) that the timer will end. 
     */
    public void setEndTime(long systemTime) {
        this.combatEnds = systemTime;
    }

    /**
     * @return The type of entity that caused this player to be tagged
     */
    public TagType getTaggedBy() {
        return this.tagType;
    }

    /**
     * @return The reason this player was tagged
     */
    public TagReason getTagReason() {
        return this.tagReason;
    }
}