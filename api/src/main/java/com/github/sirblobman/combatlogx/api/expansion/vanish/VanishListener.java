package com.github.sirblobman.combatlogx.api.expansion.vanish;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;

public final class VanishListener extends VanishExpansionListener {
    public VanishListener(@NotNull VanishExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        VanishHandler<?> vanishHandler = getVanishHandler();
        if (vanishHandler.isVanished(player) && isPreventSelfTag()) {
            e.setCancelled(true);
            return;
        }

        Entity enemy = e.getEnemy();
        if (enemy instanceof Player) {
            Player other = (Player) enemy;
            if (vanishHandler.isVanished(other) && isPreventOtherTag()) {
                e.setCancelled(true);
            }
        }
    }
}
