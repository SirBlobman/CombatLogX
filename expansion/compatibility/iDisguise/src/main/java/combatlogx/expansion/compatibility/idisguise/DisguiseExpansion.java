package combatlogx.expansion.compatibility.idisguise;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class DisguiseExpansion extends Expansion {
    public DisguiseExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("iDisguise", true, "5.8")) {
            ExpansionManager expansionManager = getPlugin().getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        new ListenerDisguise(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        // Do Nothing
    }
}
