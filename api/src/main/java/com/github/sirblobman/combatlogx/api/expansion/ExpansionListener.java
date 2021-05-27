package com.github.sirblobman.combatlogx.api.expansion;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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

    protected final Expansion getExpansion() {
        return this.expansion;
    }

    protected final ICombatLogX getCombatLogX() {
        Expansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    protected final JavaPlugin getPlugin() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlugin();
    }

    protected final LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
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
