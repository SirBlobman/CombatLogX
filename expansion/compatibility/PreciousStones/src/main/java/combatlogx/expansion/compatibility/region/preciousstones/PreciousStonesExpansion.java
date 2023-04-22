package combatlogx.expansion.compatibility.region.preciousstones;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class PreciousStonesExpansion extends RegionExpansion {
    private final PreciousStonesConfiguration configuration;
    private RegionHandler<?> regionHandler;

    public PreciousStonesExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new PreciousStonesConfiguration();
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("PreciousStones", true, "15");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerPreciousStones(this);
        }

        return this.regionHandler;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ConfigurationManager configurationManager = getConfigurationManager();
        getPreciousStonesConfiguration().load(configurationManager.get("config.yml"));
    }

    @Override
    public void afterEnable() {
        new ListenerPreciousStones(this).register();
    }

    public @NotNull PreciousStonesConfiguration getPreciousStonesConfiguration() {
        return this.configuration;
    }
}
