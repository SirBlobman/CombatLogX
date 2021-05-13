package combatlogx.expansion.loot.protection;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import combatlogx.expansion.loot.protection.listener.ListenerLootProtection;

public final class LootProtectionExpansion extends Expansion {
    public LootProtectionExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        new ListenerLootProtection(this).register();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }
}