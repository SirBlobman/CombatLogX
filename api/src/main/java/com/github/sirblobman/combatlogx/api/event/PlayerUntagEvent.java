package com.github.sirblobman.combatlogx.api.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.api.utility.Validate;
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

    private final UntagReason untagReason;
    private final List<Entity> previousEnemyList;

    public PlayerUntagEvent(Player player, UntagReason untagReason, List<Entity> previousEnemyList) {
        super(player);
        this.untagReason = untagReason;

        Validate.notNull(previousEnemyList, "previousEnemyList must not be null!");
        this.previousEnemyList = new ArrayList<>(previousEnemyList);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * @return The reason that the player was removed from combat.
     * @see #getPlayer()
     */
    public UntagReason getUntagReason() {
        return this.untagReason;
    }

    public List<Entity> getPreviousEnemies() {
        return Collections.unmodifiableList(this.previousEnemyList);
    }
}
