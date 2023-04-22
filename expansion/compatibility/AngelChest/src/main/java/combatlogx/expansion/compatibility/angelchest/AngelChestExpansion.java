package combatlogx.expansion.compatibility.angelchest;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class AngelChestExpansion extends Expansion {
    private final AngelChestConfiguration configuration;

    public AngelChestExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new AngelChestConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("AngelChest", true)) {
            selfDisable();
            return;
        }

        reloadConfig();
        new ListenerAngelChest(this).register();
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

    public @NotNull AngelChestConfiguration getConfiguration() {
        return this.configuration;
    }
}
