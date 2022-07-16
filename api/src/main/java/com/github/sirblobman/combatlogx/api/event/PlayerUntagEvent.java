package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

/**
 * A custom event that is fired when a player is removed from combat.
 *
 * @author SirBlobman
 */
public final class PlayerUntagEvent extends CustomPlayerEvent {
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

    private final UntagReason untagReason;

    public PlayerUntagEvent(Player player, UntagReason untagReason) {
        super(player);
        this.untagReason = untagReason;
    }

    /**
     * @return The reason that the player was removed from combat.
     * @see #getPlayer()
     */
    public UntagReason getUntagReason() {
        return this.untagReason;
    }
}
