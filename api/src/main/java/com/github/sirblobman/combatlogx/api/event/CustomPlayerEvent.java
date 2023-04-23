package com.github.sirblobman.combatlogx.api.event;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class CustomPlayerEvent extends PlayerEvent {
    public CustomPlayerEvent(@NotNull Player player) {
        super(player);
    }
}
