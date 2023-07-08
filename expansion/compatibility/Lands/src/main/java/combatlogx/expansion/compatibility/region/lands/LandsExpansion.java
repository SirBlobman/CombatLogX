package combatlogx.expansion.compatibility.region.lands;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

import combatlogx.expansion.compatibility.region.lands.listener.ListenerLands;

public final class LandsExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public LandsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Lands", true, "6");
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
}
