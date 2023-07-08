package com.github.sirblobman.combatlogx.api.expansion.skyblock;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class SkyBlockExpansionListener extends ExpansionListener {
    private final SkyBlockExpansion expansion;

    public SkyBlockExpansionListener(@NotNull SkyBlockExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final @NotNull SkyBlockExpansion getSkyBlockExpansion() {
        return this.expansion;
    }

    protected final @NotNull SkyBlockHandler<?> getSkyBlockHandler() {
        SkyBlockExpansion expansion = getSkyBlockExpansion();
        return expansion.getSkyBlockHandler();
    }
}
