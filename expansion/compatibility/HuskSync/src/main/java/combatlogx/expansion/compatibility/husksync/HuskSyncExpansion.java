package combatlogx.expansion.compatibility.husksync;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import java.util.HashSet;
import java.util.Set;

public final class HuskSyncExpansion extends Expansion {
    public HuskSyncExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        // Do Nothing
    }

    @Override
    public void onEnable() {
        if (!checkDependency("HuskSync", true, "3.8")) {
            selfDisable();
            return;
        }

        new ListenerHuskSync(this).register();
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
