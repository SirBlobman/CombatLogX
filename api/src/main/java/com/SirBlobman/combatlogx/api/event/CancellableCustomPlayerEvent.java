package com.SirBlobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

abstract class CancellableCustomPlayerEvent extends CustomPlayerEvent implements Cancellable {
    private boolean isCancelled;
    public CancellableCustomPlayerEvent(Player player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}