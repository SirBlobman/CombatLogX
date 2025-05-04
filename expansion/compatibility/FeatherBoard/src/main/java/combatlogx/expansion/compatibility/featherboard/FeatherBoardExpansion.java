package combatlogx.expansion.compatibility.featherboard;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.compatibility.featherboard.listener.ListenerFeatherBoard;

public final class FeatherBoardExpansion extends Expansion {
    private final FeatherBoardConfiguration configuration;

    public FeatherBoardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new FeatherBoardConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("FeatherBoard", true, "6")) {
            selfDisable();
            return;
        }

        new ListenerFeatherBoard(this).register();
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

    public @NotNull FeatherBoardConfiguration getConfiguration() {
        return this.configuration;
    }
}
