package com.github.sirblobman.combatlogx.api.expansion.disguise;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionWithDependencies;

public abstract class DisguiseExpansion extends ExpansionWithDependencies {
    public DisguiseExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public final void onCheckedEnable() {
        reloadConfig();
        registerListeners();
        afterEnable();
    }

    @Override
    public final void onCheckedDisable() {
        afterDisable();
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    private void registerListeners() {
        new DisguiseListener(this).register();
    }

    /**
     * You can override this method if you need to do something when the expansion is enabled.
     */
    public void afterEnable() {
        // Do Nothing
    }

    /**
     * You can override this method if you need to do something when the expansion is disabled.
     */
    public void afterDisable() {
        // Do Nothing
    }

    public abstract @NotNull DisguiseHandler<?> getDisguiseHandler();
}
