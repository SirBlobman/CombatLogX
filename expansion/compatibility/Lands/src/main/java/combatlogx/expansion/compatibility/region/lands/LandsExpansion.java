package combatlogx.expansion.compatibility.region.lands;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import combatlogx.expansion.compatibility.region.lands.listener.ListenerLands;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class LandsExpansion extends RegionExpansion {
    private final LandsConfiguration configuration;
    private RegionHandler<?> regionHandler;

    public LandsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.configuration = new LandsConfiguration();
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Lands", true, "7");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("lands.yml");
    }

    @Override
    public void afterEnable() {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        Optional<Expansion> optionalNewbieHelper = expansionManager.getExpansion("NewbieHelper");
        if (optionalNewbieHelper.isPresent()) {
            new ListenerLands(this).register();
        }
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerLands(this);
        }

        return this.regionHandler;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("lands.yml");
        getLandsConfiguration().load(configurationManager.get("lands.yml"));
    }

    public @NotNull LandsConfiguration getLandsConfiguration() {
        return this.configuration;
    }
}
