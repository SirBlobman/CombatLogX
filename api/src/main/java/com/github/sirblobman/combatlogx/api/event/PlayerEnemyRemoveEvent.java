package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;

/**
 * A custom event that is fired when a player is removed from combat with a specific enemy.
 * The event may not be called for enemy entities that have already been removed from the server.
 *
 * @author SirBlobman
 * @see PlayerUntagEvent
 */
public final class PlayerEnemyRemoveEvent extends CustomPlayerEvent {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final UntagReason untagReason;
    private final Entity enemy;

    public PlayerEnemyRemoveEvent(@NotNull Player player, @NotNull UntagReason untagReason, @NotNull Entity enemy) {
        super(player);
        this.untagReason = untagReason;
        this.enemy = enemy;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * @return The reason that the player was removed from combat with the enemy.
     * @see #getPlayer()
     */
    public @NotNull UntagReason getUntagReason() {
        return this.untagReason;
    }

    /**
     * @return The previous enemy of the player./
     */
    public @NotNull Entity getEnemy() {
        return this.enemy;
    }
}
