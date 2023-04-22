package com.github.sirblobman.combatlogx.api.expansion.skyblock;

import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.OfflinePlayer;

public abstract class IslandWrapper {
    public abstract @NotNull Set<UUID> getMembers();

    public boolean isMember(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        Set<UUID> memberIdSet = getMembers();
        return memberIdSet.contains(playerId);
    }
}
