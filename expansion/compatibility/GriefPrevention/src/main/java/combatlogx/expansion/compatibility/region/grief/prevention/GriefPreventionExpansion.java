package combatlogx.expansion.compatibility.region.grief.prevention;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class GriefPreventionExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public GriefPreventionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("GriefPrevention", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new GriefPreventionRegionHandler(this);
        }

        return this.regionHandler;
    }
}
