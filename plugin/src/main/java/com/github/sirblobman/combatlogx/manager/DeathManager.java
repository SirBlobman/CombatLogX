package com.github.sirblobman.combatlogx.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

public class DeathManager implements IDeathManager {
    private final Set<UUID> killedPlayersSet;

    public DeathManager() {
        this.killedPlayersSet = new HashSet<>();
    }

    @Override
    public void kill(final Player player) {
        UUID uuid = player.getUniqueId();
        this.killedPlayersSet.add(uuid);
        player.setHealth(0.0D);
    }

    @Override
    public boolean wasPunishKilled(final Player player) {
        UUID uuid = player.getUniqueId();
        return this.killedPlayersSet.contains(uuid);
    }

    @Override
    public boolean stopTracking(final Player player) {
        UUID uuid = player.getUniqueId();
        return this.killedPlayersSet.remove(uuid);
    }
}
