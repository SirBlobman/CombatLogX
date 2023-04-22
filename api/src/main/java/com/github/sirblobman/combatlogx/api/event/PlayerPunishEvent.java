package com.github.sirblobman.combatlogx.api.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;

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
    private final List<Entity> enemyList;

    public PlayerPunishEvent(@NotNull Player player, @NotNull UntagReason punishReason,
                             @NotNull List<Entity> enemyList) {
        super(player);
        this.punishReason = punishReason;
        this.enemyList = new ArrayList<>(enemyList);
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * @return The original {@link UntagReason} that the player was punished for.
     */
    public @NotNull UntagReason getPunishReason() {
        return this.punishReason;
    }

    /**
     * @return The list of enemies the player had when punished.
     */
    public @NotNull List<Entity> getEnemies() {
        return Collections.unmodifiableList(this.enemyList);
    }
}
