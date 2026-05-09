package combatlogx.expansion.damage.effects;

import com.github.sirblobman.api.utility.VersionUtility;
import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import java.util.logging.Logger;

public final class DamageEffectsExpansion extends Expansion {
    private final DamageEffectsConfiguration configuration;

    public DamageEffectsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new DamageEffectsConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("effects.yml");
    }

    @Override
    public void onEnable() {
        int majorVersion = VersionUtility.getMajorVersion();
        int minorVersion = VersionUtility.getMinorVersion();
        if (majorVersion == 1 && minorVersion < 13) {
            Logger logger = getLogger();
            logger.warning("This expansion requires Spigot 1.13 or higher.");
            selfDisable();
            return;
        }

        reloadConfig();
        new ListenerDamageEffects(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("effects.yml");
        getConfiguration().load(configurationManager.get("effects.yml"));
    }

    public @NotNull DamageEffectsConfiguration getConfiguration() {
        return this.configuration;
    }
}
