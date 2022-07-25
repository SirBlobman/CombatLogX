package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.api.utility.Validate;
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

    public PlayerEnemyRemoveEvent(Player player, UntagReason untagReason, Entity enemy) {
        super(player);

        this.untagReason = Validate.notNull(untagReason, "untagReason must not be null!");
        this.enemy = Validate.notNull(enemy, "enemy must not be null!");
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The reason that the player was removed from combat with the enemy.
     * @see #getPlayer()
     */
    @NotNull
    public UntagReason getUntagReason() {
        return this.untagReason;
    }

    /**
     * @return The previous enemy of the player./
     */
    @NotNull
    public Entity getEnemy() {
        return this.enemy;
    }
}
