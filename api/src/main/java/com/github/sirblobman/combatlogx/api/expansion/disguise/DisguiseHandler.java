package com.github.sirblobman.combatlogx.api.expansion.disguise;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

public abstract class DisguiseHandler<DE extends DisguiseExpansion> {
    private final DE expansion;

    public DisguiseHandler(@NotNull DE expansion) {
        this.expansion = expansion;
    }

    protected final DE getExpansion() {
        return this.expansion;
    }

    public abstract boolean hasDisguise(@NotNull Player player);

    public abstract void removeDisguise(@NotNull Player player);
}
