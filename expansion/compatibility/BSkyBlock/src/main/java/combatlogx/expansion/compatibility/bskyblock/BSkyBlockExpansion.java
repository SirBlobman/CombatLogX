package combatlogx.expansion.compatibility.bskyblock;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.bskyblock.hook.HookBentoBox;
import combatlogx.expansion.compatibility.bskyblock.listener.ListenerBSkyBlock;

public final class BSkyBlockExpansion extends Expansion {
    public BSkyBlockExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("BentoBox", true)) {
            selfDisable();
            return;
        }

        if (!HookBentoBox.findBSkyBlock(this)) {
            selfDisable();
            return;
        }

        new ListenerBSkyBlock(this).register();
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
