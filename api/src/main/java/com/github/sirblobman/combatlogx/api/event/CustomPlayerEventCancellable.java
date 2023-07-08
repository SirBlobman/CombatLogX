package com.github.sirblobman.combatlogx.api.event;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class CustomPlayerEventCancellable extends CustomPlayerEvent implements Cancellable {
    private boolean cancelled;

    public CustomPlayerEventCancellable(@NotNull Player player) {
        super(player);
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
