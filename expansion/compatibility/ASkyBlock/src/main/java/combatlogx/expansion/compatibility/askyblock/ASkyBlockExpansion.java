package combatlogx.expansion.compatibility.askyblock;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.askyblock.listener.ListenerASkyBlock;

public final class ASkyBlockExpansion extends Expansion {
    public ASkyBlockExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("ASkyBlock", true)) {
            selfDisable();
        }

        new ListenerASkyBlock(this).register();
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
