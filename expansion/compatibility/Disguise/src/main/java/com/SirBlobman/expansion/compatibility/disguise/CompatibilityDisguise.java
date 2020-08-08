package com.SirBlobman.expansion.compatibility.disguise;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.expansion.compatibility.disguise.listener.ListenerDisguise;

public class CompatibilityDisguise extends Expansion {
    public CompatibilityDisguise(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        expansionManager.registerListener(this, new ListenerDisguise(this));

        printHookInfo("iDisguise");
        printHookInfo("LibsDisguises");
    }
}