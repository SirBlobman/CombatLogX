package com.github.sirblobman.combatlogx.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;

public final class PlayerVulnerableTask extends EntityTaskDetails<ConfigurablePlugin, Player> {
    public PlayerVulnerableTask(@NotNull ConfigurablePlugin plugin, @NotNull Player entity) {
        super(plugin, entity);
    }

    @Override
    public void run() {
        Player entity = getEntity();
        if (entity != null) {
            entity.setNoDamageTicks(0);
        }
    }
}
