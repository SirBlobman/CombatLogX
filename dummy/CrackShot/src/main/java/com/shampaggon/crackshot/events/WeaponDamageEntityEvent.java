package com.shampaggon.crackshot.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

public class WeaponDamageEntityEvent extends Event {
    public Player getPlayer() {
        throw new UnsupportedOperationException("Dummy Method");
    }

    public Entity getVictim() {
        throw new UnsupportedOperationException("Dummy Method");
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new UnsupportedOperationException("Dummy Method");
    }
}