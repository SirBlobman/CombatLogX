package combatlogx.expansion.damage.tagger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.damage.tagger.configuration.DamageTaggerConfiguration;
import combatlogx.expansion.damage.tagger.listener.ListenerDamage;

public final class DamageTaggerExpansion extends Expansion {
    private final DamageTaggerConfiguration configuration;

    public DamageTaggerExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new DamageTaggerConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();
        new ListenerDamage(this).register();
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

    public DamageTaggerConfiguration getConfiguration() {
        return this.configuration;
    }
}
