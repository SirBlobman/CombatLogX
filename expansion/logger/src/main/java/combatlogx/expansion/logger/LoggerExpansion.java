package combatlogx.expansion.logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.logger.configuration.LoggerConfiguration;
import combatlogx.expansion.logger.listener.ListenerLogger;

public final class LoggerExpansion extends Expansion {
    private final LoggerConfiguration configuration;

    public LoggerExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new LoggerConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();
        new ListenerLogger(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    public @NotNull LoggerConfiguration getConfiguration() {
        return this.configuration;
    }
}
