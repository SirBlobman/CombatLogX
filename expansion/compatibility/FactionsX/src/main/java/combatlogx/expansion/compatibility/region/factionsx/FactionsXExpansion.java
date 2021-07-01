package combatlogx.expansion.compatibility.region.factionsx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class FactionsXExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public FactionsXExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("FactionsX", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new FactionsXRegionHandler(this);
        }

        return this.regionHandler;
    }
}
