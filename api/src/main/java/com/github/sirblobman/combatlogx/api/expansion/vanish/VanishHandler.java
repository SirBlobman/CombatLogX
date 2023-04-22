package com.github.sirblobman.combatlogx.api.expansion.vanish;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

public abstract class VanishHandler<VE extends VanishExpansion> {
    private final VE expansion;

    public VanishHandler(@NotNull VE expansion) {
        this.expansion = expansion;
    }

    protected final VE getExpansion() {
        return this.expansion;
    }

    public abstract boolean isVanished(@NotNull Player player);
}
