package combatlogx.expansion.logger;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;

import combatlogx.expansion.logger.listener.ListenerLogger;

public final class LoggerExpansion extends Expansion {
    public LoggerExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        new ListenerLogger(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
}
