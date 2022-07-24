package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

/**
 * A custom event that will be fired before a player is put into combat.
 * If the event is cancelled, the player will not be tagged.
 *
 * @author SirBlobman
 */
public final class PlayerPreTagEvent extends CustomPlayerEventCancellable {
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

    public PlayerPreTagEvent(Player player, Entity enemy, TagType tagType, TagReason tagReason) {
        super(player);
        this.enemy = enemy;
        this.tagType = Validate.notNull(tagType, "tagType must not be null!");
        this.tagReason = Validate.notNull(tagReason, "tagReason must not be null!");
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
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
}
