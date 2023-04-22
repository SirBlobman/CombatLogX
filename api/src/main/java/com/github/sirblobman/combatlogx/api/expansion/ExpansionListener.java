package com.github.sirblobman.combatlogx.api.expansion;

import java.util.Locale;
import java.util.logging.Logger;

import com.github.sirblobman.combatlogx.api.listener.CombatListener;

import org.jetbrains.annotations.NotNull;

public abstract class ExpansionListener extends CombatListener {
    private final Expansion expansion;

    public ExpansionListener(@NotNull Expansion expansion) {
        super(expansion.getPlugin());
        this.expansion = expansion;
    }

    @Override
    public final void register() {
        Expansion expansion = getExpansion();
        expansion.registerListener(this);
    }

    @Override
    protected final void printDebug(@NotNull String message) {
        if (isDebugModeDisabled()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger expansionLogger = getExpansionLogger();
        expansionLogger.info(logMessage);
    }

    protected final @NotNull Expansion getExpansion() {
        return this.expansion;
    }

    protected final @NotNull Logger getExpansionLogger() {
        Expansion expansion = getExpansion();
        return expansion.getLogger();
    }
}
