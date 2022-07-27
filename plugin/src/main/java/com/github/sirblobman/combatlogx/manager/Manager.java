package com.github.sirblobman.combatlogx.manager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

public abstract class Manager implements ICombatLogXNeeded {
    private final ICombatLogX plugin;

    public Manager(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final ConfigurationManager getConfigurationManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getConfigurationManager();
    }

    protected final PlayerDataManager getPlayerDataManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getPlayerDataManager();
    }

    protected final void printDebug(String... messages) {
        ICombatLogX plugin = getCombatLogX();
        plugin.printDebug(messages);
    }
}
