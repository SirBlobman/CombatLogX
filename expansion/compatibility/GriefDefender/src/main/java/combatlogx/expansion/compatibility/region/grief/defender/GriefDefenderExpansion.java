package combatlogx.expansion.compatibility.region.grief.defender;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.region.RegionExpansion;
import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;

public final class GriefDefenderExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    public GriefDefenderExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("GriefDefender", true);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new GriefDefenderRegionHandler(this);
        }

        return this.regionHandler;
    }
}
