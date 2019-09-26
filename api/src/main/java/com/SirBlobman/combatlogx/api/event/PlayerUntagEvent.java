package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * PlayerUntagEvent is an event that is fired when a player is removed from combat.
 *
 * @author SirBlobman
 */
public class PlayerUntagEvent extends CustomPlayerEvent {
    public enum UntagReason {
        /** The player waited patiently until they were no longer in combat */ EXPIRE,
        /** The player's enemy died and the config option was enabled to untag them */ EXPIRE_ENEMY_DEATH,
        /** The player disconnected from the server */ QUIT,
        /** The player was kicked by a plugin or timed out */ KICK
        ;

        UntagReason() {
            this(false);
        }

        private final boolean isExpire;
        UntagReason(boolean isExpire) {
            this.isExpire = isExpire;
        }

        public boolean isExpire() {
            return this.isExpire;
        }
    }

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