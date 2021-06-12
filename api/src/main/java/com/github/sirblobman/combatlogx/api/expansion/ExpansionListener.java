package com.github.sirblobman.combatlogx.api.expansion;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

public abstract class ExpansionListener implements Listener {
    private final Expansion expansion;
    public ExpansionListener(Expansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public final void register() {
        Expansion expansion = getExpansion();
        expansion.registerListener(this);
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }

    protected final Expansion getExpansion() {
        return this.expansion;
    }

    protected final ConfigurationManager getExpansionConfigurationManager() {
        Expansion expansion = getExpansion();
        return expansion.getConfigurationManager();
    }

    protected final ICombatLogX getCombatLogX() {
        Expansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    protected final JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    protected final ConfigurationManager getPluginConfigurationManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getConfigurationManager();
    }

    protected final LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    protected final PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    protected final ICombatManager getCombatManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getCombatManager();
    }

    protected final boolean isInCombat(Player player) {
        ICombatManager combatManager = getCombatManager();
        return combatManager.isInCombat(player);
    }
}
