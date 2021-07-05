package com.github.sirblobman.combatlogx.api.expansion;

import java.util.logging.Logger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;

public abstract class ExpansionListener extends CombatListener {
    private final Expansion expansion;

    public ExpansionListener(Expansion expansion) {
        super(expansion.getPlugin());
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public final void register() {
        Expansion expansion = getExpansion();
        expansion.registerListener(this);
    }

    protected final Expansion getExpansion() {
        return this.expansion;
    }

    protected final Logger getExpansionLogger() {
        Expansion expansion = getExpansion();
        return expansion.getLogger();
    }

    protected final ConfigurationManager getExpansionConfigurationManager() {
        Expansion expansion = getExpansion();
        return expansion.getConfigurationManager();
    }
}
