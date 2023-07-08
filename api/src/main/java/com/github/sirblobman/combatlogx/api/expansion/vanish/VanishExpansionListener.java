package com.github.sirblobman.combatlogx.api.expansion.vanish;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

public abstract class VanishExpansionListener extends ExpansionListener {
    private final VanishExpansion expansion;
    ;

    public VanishExpansionListener(@NotNull VanishExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    protected final @NotNull VanishExpansion getVanishExpansion() {
        return this.expansion;
    }

    protected final @NotNull VanishHandler<?> getVanishHandler() {
        VanishExpansion expansion = getVanishExpansion();
        return expansion.getVanishHandler();
    }

    protected final @NotNull VanishExpansionConfiguration getConfiguration() {
        VanishExpansion expansion = getVanishExpansion();
        return expansion.getConfiguration();
    }

    protected final boolean isPreventSelfTag() {
        VanishExpansionConfiguration configuration = getConfiguration();
        return configuration.isPreventVanishTaggingSelf();
    }

    protected final boolean isPreventOtherTag() {
        VanishExpansionConfiguration configuration = getConfiguration();
        return configuration.isPreventVanishTaggingOther();
    }
}
