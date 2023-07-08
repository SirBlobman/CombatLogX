package com.github.sirblobman.combatlogx.api.expansion;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.sirblobman.combatlogx.api.listener.CombatListener;

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
        printDebug(message, null);
    }

    protected final void printDebug(@NotNull String message, @Nullable Throwable throwable) {
        if (isDebugModeDisabled()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger expansionLogger = getExpansionLogger();
        if (throwable == null) {
            expansionLogger.info(logMessage);
        } else {
            expansionLogger.log(Level.WARNING, logMessage, throwable);
        }
    }

    protected final @NotNull Expansion getExpansion() {
        return this.expansion;
    }

    protected final @NotNull Logger getExpansionLogger() {
        Expansion expansion = getExpansion();
        return expansion.getLogger();
    }
}
