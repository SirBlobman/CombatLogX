package combatlogx.expansion.compatibility.region.husktowns;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class HuskTownsExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public HuskTownsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("HuskTowns", true, "2.1");
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new HuskTownsRegionHandler(this);
        }

        return this.regionHandler;
    }
}
