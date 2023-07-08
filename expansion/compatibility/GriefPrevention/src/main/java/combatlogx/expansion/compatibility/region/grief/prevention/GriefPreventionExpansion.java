package combatlogx.expansion.compatibility.region.grief.prevention;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class GriefPreventionExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public GriefPreventionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("GriefPrevention", true);
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerGriefPrevention(this);
        }

        return this.regionHandler;
    }
}
