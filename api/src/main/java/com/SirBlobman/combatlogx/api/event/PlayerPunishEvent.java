package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * PlayerPunishEvent is an event that will fire before a player is punished
 * If the event is cancelled, the player will not be punished.
 *
 * @author SirBlobman
 */
public class PlayerPunishEvent extends CancellableCustomPlayerEvent {
    private final PlayerUntagEvent.UntagReason punishReason;
    private final LivingEntity previousEnemy;
    public PlayerPunishEvent(Player player, PlayerUntagEvent.UntagReason punishReason, LivingEntity previousEnemy) {
        super(player);
        this.punishReason = punishReason;
        this.previousEnemy = previousEnemy;
    }

    /**
     * @return The previous enemy of the player, or null if one did not exist.
     */
    public LivingEntity getPreviousEnemy() {
        return this.previousEnemy;
    }

    /**
     * @return The original {@link com.SirBlobman.combatlogx.api.event.PlayerUntagEvent.UntagReason} that the player was punished for.
     */
    public PlayerUntagEvent.UntagReason getPunishReason() {
        return this.punishReason;
    }
}