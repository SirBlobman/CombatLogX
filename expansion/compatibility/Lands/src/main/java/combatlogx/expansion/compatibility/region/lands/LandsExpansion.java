package combatlogx.expansion.compatibility.region.lands;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class LandsExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    
    public LandsExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }
    
    @Override
    public boolean checkDependencies() {
        return checkDependency("Lands", true);
    }
    
    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new LandsRegionHandler(this);
        }
        
        return this.regionHandler;
    }
}
