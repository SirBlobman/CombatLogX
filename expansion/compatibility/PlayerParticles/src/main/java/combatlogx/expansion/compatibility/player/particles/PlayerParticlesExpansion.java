package combatlogx.expansion.compatibility.player.particles;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class PlayerParticlesExpansion extends Expansion {
    private final PlayerParticlesConfiguration configuration;

    public PlayerParticlesExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new PlayerParticlesConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        if (!checkDependency("PlayerParticles", true, "8")) {
            selfDisable();
            return;
        }

        new ListenerPlayerParticles(this).register();
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        getConfiguration().load(configurationManager.get("config.yml"));
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    public @NotNull PlayerParticlesConfiguration getConfiguration() {
        return this.configuration;
    }
}
