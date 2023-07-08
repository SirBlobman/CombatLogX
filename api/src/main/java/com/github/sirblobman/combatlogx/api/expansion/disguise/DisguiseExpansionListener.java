package com.github.sirblobman.combatlogx.api.expansion.disguise;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class DisguiseExpansionListener extends ExpansionListener {
    private final DisguiseExpansion expansion;

    public DisguiseExpansionListener(@NotNull DisguiseExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final @NotNull DisguiseExpansion getDisguiseExpansion() {
        return this.expansion;
    }

    protected final @NotNull DisguiseHandler<?> getDisguiseHandler() {
        DisguiseExpansion expansion = getDisguiseExpansion();
        return expansion.getDisguiseHandler();
    }
}
