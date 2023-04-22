package combatlogx.expansion.loot.protection;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.loot.protection.configuration.LootProtectionConfiguration;
import combatlogx.expansion.loot.protection.listener.ListenerLootProtection;

public final class LootProtectionExpansion extends Expansion {
    private final LootProtectionConfiguration configuration;

    public LootProtectionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new LootProtectionConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 16) {
            Logger logger = getLogger();
            logger.info("The loot protection expansion requires Spigot 1.16.5 or higher.");
            selfDisable();
            return;
        }

        reloadConfig();
        new ListenerLootProtection(this).register();
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

    public @NotNull LootProtectionConfiguration getConfiguration() {
        return this.configuration;
    }
}
