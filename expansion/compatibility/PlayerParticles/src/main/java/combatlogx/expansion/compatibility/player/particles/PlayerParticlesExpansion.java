package combatlogx.expansion.compatibility.player.particles;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class PlayerParticlesExpansion extends Expansion {
    public PlayerParticlesExpansion(ICombatLogX plugin) {
        super(plugin);
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
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }
}
