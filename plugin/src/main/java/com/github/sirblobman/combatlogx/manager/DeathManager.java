package com.github.sirblobman.combatlogx.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

public final class DeathManager extends Manager implements IDeathManager {
    private final Set<UUID> killedPlayerSet;

    public DeathManager(ICombatLogX plugin) {
        super(plugin);
        this.killedPlayerSet = new HashSet<>();
    }

    @Override
    public void kill(Player player) {
        UUID playerId = player.getUniqueId();
        this.killedPlayerSet.add(playerId);
        player.setHealth(0.0D);
    }

    @Override
    public boolean wasPunishKilled(Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerSet.contains(playerId);
    }

    @Override
    public boolean stopTracking(final Player player) {
        UUID playerId = player.getUniqueId();
        return this.killedPlayerSet.remove(playerId);
    }
}
