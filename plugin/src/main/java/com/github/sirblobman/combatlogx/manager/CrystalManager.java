package com.github.sirblobman.combatlogx.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;

public final class CrystalManager extends Manager implements ICrystalManager {
    private final Map<UUID, UUID> endCrystalMap;

    public CrystalManager(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.endCrystalMap = new ConcurrentHashMap<>();
    }

    @Override
    public @Nullable Player getPlacer(@NotNull Entity crystal) {
        UUID entityId = crystal.getUniqueId();
        UUID playerId = this.endCrystalMap.get(entityId);
        if (playerId == null) {
            return null;
        }

        return Bukkit.getPlayer(playerId);
    }

    @Override
    public void setPlacer(@NotNull Entity crystal, @NotNull Player player) {
        UUID entityId = crystal.getUniqueId();
        UUID playerId = player.getUniqueId();
        this.endCrystalMap.put(entityId, playerId);
    }

    @Override
    public void remove(@NotNull UUID crystalId) {
        this.endCrystalMap.remove(crystalId);
    }
}
