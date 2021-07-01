package combatlogx.expansion.compatibility.region.residence;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class ResidenceExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public ResidenceExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Residence", false, "4.9");
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new ResidenceRegionHandler(this);
        }

        return this.regionHandler;
    }
}
