package combatlogx.expansion.compatibility.region.legacy.factions;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class LegacyFactionsExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public LegacyFactionsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("LegacyFactions", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new FactionsRegionHandler(this);
        }

        return this.regionHandler;
    }
}
