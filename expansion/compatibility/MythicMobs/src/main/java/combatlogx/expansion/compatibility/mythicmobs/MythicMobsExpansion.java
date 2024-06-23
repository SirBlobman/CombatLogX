package combatlogx.expansion.compatibility.mythicmobs;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class MythicMobsExpansion extends Expansion {
    private final MythicMobsConfiguration configuration;

    public MythicMobsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new MythicMobsConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("MythicMobs", true, "5.6")) {
            selfDisable();
            return;
        }

        reloadConfig();
        new ListenerMythicMobs(this).register();
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

    public @NotNull MythicMobsConfiguration getConfiguration() {
        return this.configuration;
    }
}
