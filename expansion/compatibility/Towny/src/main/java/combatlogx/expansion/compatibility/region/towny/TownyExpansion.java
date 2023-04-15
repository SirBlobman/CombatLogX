package combatlogx.expansion.compatibility.region.towny;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class TownyExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public TownyExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Towny", true, "0.99");
    }

    @Override
    public void afterEnable() {
        checkDependency("FlagWar", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new TownyRegionHandler(this);
        }

        return this.regionHandler;
    }
}
