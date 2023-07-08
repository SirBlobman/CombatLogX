package combatlogx.expansion.compatibility.marriagemaster;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.marriagemaster.listener.ListenerMarriageMaster;

public final class MarriageMasterExpansion extends Expansion {
    public MarriageMasterExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("MarriageMaster", true)) {
            selfDisable();
            return;
        }

        new ListenerMarriageMaster(this).register();
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
