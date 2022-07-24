package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

/**
 * A custom event that will fire before a player is punished.
 * If the event is cancelled, the player will not be punished.
 *
 * @author SirBlobman
 */
public final class PlayerPunishEvent extends CustomPlayerEventCancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final UntagReason punishReason;
    private final Entity previousEnemy;

    public PlayerPunishEvent(Player player, UntagReason punishReason, Entity previousEnemy) {
        super(player);
        this.previousEnemy = previousEnemy;
        this.punishReason = Validate.notNull(punishReason, "punishReason must not be null!");
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The previous enemy of the player, or null if one did not exist.
     */
    public Entity getPreviousEnemy() {
        return this.previousEnemy;
    }

    /**
     * @return The original {@link UntagReason} that the player was punished for.
     */
    public UntagReason getPunishReason() {
        return this.punishReason;
    }
}
