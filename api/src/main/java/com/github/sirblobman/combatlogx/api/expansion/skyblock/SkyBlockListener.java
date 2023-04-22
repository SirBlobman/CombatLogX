package com.github.sirblobman.combatlogx.api.expansion.skyblock;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;

public final class SkyBlockListener extends SkyBlockExpansionListener {
    public SkyBlockListener(@NotNull SkyBlockExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        Entity enemy = e.getEnemy();
        if (!(enemy instanceof Player)) {
            return;
        }

        Player player = e.getPlayer();
        Player enemyPlayer = (Player) enemy;
        SkyBlockHandler<?> handler = getSkyBlockHandler();
        if (handler.doesIslandMatch(player, enemyPlayer)) {
            e.setCancelled(true);
        }
    }
}
