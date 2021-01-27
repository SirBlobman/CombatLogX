package combatlogx.expansion.compatibility.iridium.skyblock;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.compatibility.iridium.skyblock.listener.ListenerIridiumSkyblock;

public class IridiumSkyblockExpansion extends Expansion {
    public IridiumSkyblockExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        if(!checkDependency("IridiumSkyblock", true)) {
            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        new ListenerIridiumSkyblock(this).register();
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
}