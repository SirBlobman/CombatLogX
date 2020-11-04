package combatlogx.expansion.compatibility.mvdwplaceholderapi;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

public final class MVdWPlaceholderAPIExpansion extends Expansion {
    public MVdWPlaceholderAPIExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if(!checkDependency("MVdWPlaceholderAPI", true)) {
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        HookMVdWPlaceholderAPI hook = new HookMVdWPlaceholderAPI(this);
        hook.register();
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