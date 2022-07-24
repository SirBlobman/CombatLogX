package combatlogx.expansion.compatibility.fabled.skyblock;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.fabled.skyblock.listener.ListenerFabledSkyBlock;

public final class FabledSkyBlockExpansion extends Expansion {
    public FabledSkyBlockExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("FabledSkyBlock", true)) {
            selfDisable();
            return;
        }

        new ListenerFabledSkyBlock(this).register();
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
