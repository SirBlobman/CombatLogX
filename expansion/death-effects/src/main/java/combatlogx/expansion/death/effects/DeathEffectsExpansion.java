package combatlogx.expansion.death.effects;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class DeathEffectsExpansion extends Expansion {
    private final DeathEffectsConfiguration configuration;

    public DeathEffectsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new DeathEffectsConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();
        new ListenerDeathEffects(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        YamlConfiguration configuration = configurationManager.get("config.yml");
        DeathEffectsConfiguration deathEffectsConfiguration = getConfiguration();
        deathEffectsConfiguration.load(configuration);
    }

    public DeathEffectsConfiguration getConfiguration() {
        return this.configuration;
    }
}
