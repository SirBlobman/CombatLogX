package com.github.sirblobman.combatlogx.api.expansion;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;

public abstract class ExpansionWithDependencies extends Expansion {
    private boolean enabledSuccessfully;

    public ExpansionWithDependencies(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.enabledSuccessfully = false;
    }

    @Override
    public final void onEnable() {
        ICombatLogX plugin = getPlugin();
        if (!checkDependencies()) {
            Logger logger = getLogger();
            logger.info("Some dependencies for this expansion are missing!");
            selfDisable();
            return;
        }

        onCheckedEnable();
        this.enabledSuccessfully = true;
    }

    @Override
    public final void onDisable() {
        if (!isEnabledSuccessfully()) {
            return;
        }

        onCheckedDisable();
        this.enabledSuccessfully = false;
    }

    public boolean isEnabledSuccessfully() {
        return this.enabledSuccessfully;
    }

    public abstract boolean checkDependencies();
    public abstract void onCheckedEnable();
    public abstract void onCheckedDisable();
}
