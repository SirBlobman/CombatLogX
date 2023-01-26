package combatlogx.expansion.compatibility.huskhomes;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.huskhomes.listener.ListenerHuskHomes;

public final class HuskHomesExpansion extends Expansion {
    public HuskHomesExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("HuskHomes", true, "3.2")) {
            selfDisable();
            return;
        }

        new ListenerHuskHomes(this).register();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
}
