package combatlogx.expansion.compatibility.region.protectionstones;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class ProtectionStonesExpansion extends RegionExpansion {
    private final ProtectionStonesConfiguration configuration;
    private RegionHandler<?> regionHandler;

    public ProtectionStonesExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new ProtectionStonesConfiguration();
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        boolean checkProtectionStones = checkDependency("ProtectionStones", true);
        boolean checkWorldGuard = checkDependency("WorldGuard", true, "7");
        return (checkProtectionStones && checkWorldGuard);
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerProtectionStones(this);
        }

        return this.regionHandler;
    }

    @Override
    public void afterEnable() {
        new ProtectionStonesListener(this).register();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigurationManager configurationManager = getConfigurationManager();
        getProtectionStonesConfiguration().load(configurationManager.get("config.yml"));
    }

    public @NotNull ProtectionStonesConfiguration getProtectionStonesConfiguration() {
        return this.configuration;
    }
}
