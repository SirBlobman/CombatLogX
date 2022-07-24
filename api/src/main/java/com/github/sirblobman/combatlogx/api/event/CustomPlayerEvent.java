package com.github.sirblobman.combatlogx.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class CustomPlayerEvent extends PlayerEvent {
    public CustomPlayerEvent(Player player) {
        super(player);
    }
}
