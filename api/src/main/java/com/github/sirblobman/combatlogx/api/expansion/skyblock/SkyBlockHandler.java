package com.github.sirblobman.combatlogx.api.expansion.skyblock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public abstract class SkyBlockHandler<SE extends SkyBlockExpansion> {
    private final SE expansion;

    public SkyBlockHandler(@NotNull SE expansion) {
        this.expansion = expansion;
    }

    protected final SE getExpansion() {
        return this.expansion;
    }

    public abstract @Nullable IslandWrapper getIsland(@NotNull Location location);

    public abstract @Nullable IslandWrapper getIsland(@NotNull OfflinePlayer player);

    public abstract boolean doesIslandMatch(@NotNull OfflinePlayer player1, @NotNull OfflinePlayer player2);
}
