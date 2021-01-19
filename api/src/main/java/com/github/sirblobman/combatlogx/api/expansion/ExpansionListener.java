package com.github.sirblobman.combatlogx.api.expansion;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

public abstract class ExpansionListener implements Listener {
    private final Expansion expansion;
    public ExpansionListener(Expansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    public final Expansion getExpansion() {
        return this.expansion;
    }

    public final ICombatLogX getCombatLogX() {
        Expansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    public final JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    public final LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    public final void register() {
        Expansion expansion = getExpansion();
        expansion.registerListener(this);
    }
}