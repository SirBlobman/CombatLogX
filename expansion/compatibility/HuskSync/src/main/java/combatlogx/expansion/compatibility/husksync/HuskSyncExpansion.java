package combatlogx.expansion.compatibility.husksync;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class HuskSyncExpansion extends Expansion {

    public HuskSyncExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        ICombatLogX clx = getPlugin();
        if(!checkDependency("HuskSync", true)) {
            ExpansionManager expansionManager = clx.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        registerListener(new ExpansionListener(clx.getPlugin(), this));
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
