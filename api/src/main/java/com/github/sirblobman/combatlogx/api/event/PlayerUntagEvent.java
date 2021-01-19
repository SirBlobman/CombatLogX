package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

/**
 * {@link PlayerUntagEvent} is an event that is fired when a player is removed from combat.
 * @author SirBlobman
 */
public class PlayerUntagEvent extends CustomPlayerEvent {
    private final UntagReason untagReason;
    private final LivingEntity previousEnemy;
    public PlayerUntagEvent(Player player, UntagReason untagReason, LivingEntity previousEnemy) {
        super(player);
        this.untagReason = untagReason;
        this.previousEnemy = previousEnemy;
    }

    /**
     * @return The reason that the player was removed from combat.
     * @see #getPlayer()
     */
    public UntagReason getUntagReason() {
        return this.untagReason;
    }

    /**
     * @return The previous enemy of the player, or null if one did not exist.
     */
    public LivingEntity getPreviousEnemy() {
        return this.previousEnemy;
    }
}