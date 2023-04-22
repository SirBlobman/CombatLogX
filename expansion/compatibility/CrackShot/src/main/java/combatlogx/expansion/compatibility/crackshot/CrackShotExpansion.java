package combatlogx.expansion.compatibility.crackshot;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.crackshot.listener.ListenerCrackShot;

public final class CrackShotExpansion extends Expansion {
    public CrackShotExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("CrackShot", true)) {
            selfDisable();
            return;
        }

        new ListenerCrackShot(this).register();
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
