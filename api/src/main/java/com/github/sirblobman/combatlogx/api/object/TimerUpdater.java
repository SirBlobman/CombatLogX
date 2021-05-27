package com.github.sirblobman.combatlogx.api.object;

import org.bukkit.entity.Player;

public interface TimerUpdater {
    void update(Player player, long timeLeftMillis);
    void remove(Player player);
}
