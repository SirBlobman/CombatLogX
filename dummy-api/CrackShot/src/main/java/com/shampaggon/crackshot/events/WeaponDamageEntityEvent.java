package com.shampaggon.crackshot.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WeaponDamageEntityEvent extends Event implements Cancellable {
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Dummy API");
    }

    @Override
    public void setCancelled(boolean cancel) {
        throw new UnsupportedOperationException("Dummy API");
    }

    @Override
    public HandlerList getHandlers() {
        throw new UnsupportedOperationException("Dummy API");
    }

    public Entity getVictim() {
        throw new UnsupportedOperationException("Dummy API");
    }

    public Player getPlayer() {
        throw new UnsupportedOperationException("Dummy API");
    }
}