package combatlogx.expansion.compatibility.superior.skyblock;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.superior.skyblock.listener.ListenerSuperiorSkyblock;

public final class SuperiorSkyblockExpansion extends Expansion {
    public SuperiorSkyblockExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("SuperiorSkyblock2", true)) {
            selfDisable();
            return;
        }

        new ListenerSuperiorSkyblock(this).register();
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
