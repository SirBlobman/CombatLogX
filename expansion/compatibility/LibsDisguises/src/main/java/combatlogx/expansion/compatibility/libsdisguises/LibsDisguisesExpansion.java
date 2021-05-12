package combatlogx.expansion.compatibility.libsdisguises;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public class LibsDisguisesExpansion extends Expansion {
    public LibsDisguisesExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if(!checkDependency("LibsDisguises", true, "10")) {
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
