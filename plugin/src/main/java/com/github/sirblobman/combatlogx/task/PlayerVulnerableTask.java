package com.github.sirblobman.combatlogx.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

public final class PlayerVulnerableTask extends EntityTaskDetails<Player> {
    public PlayerVulnerableTask(@NotNull ICombatLogX plugin, @NotNull Player entity) {
        super(plugin.getPlugin(), entity);
    }

    @Override
    public void run() {
        Player entity = getEntity();
        if (entity != null) {
            entity.setNoDamageTicks(0);
        }
    }
}
