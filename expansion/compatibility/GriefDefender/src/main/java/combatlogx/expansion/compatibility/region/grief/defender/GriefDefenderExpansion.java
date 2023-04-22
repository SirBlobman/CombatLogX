package combatlogx.expansion.compatibility.region.grief.defender;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class GriefDefenderExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public GriefDefenderExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("GriefDefender", true);
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerGriefDefender(this);
        }

        return this.regionHandler;
    }
}
