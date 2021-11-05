package combatlogx.expansion.compatibility.region.kingdomsx;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class KingdomsXExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    
    public KingdomsXExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }
    
    @Override
    public boolean checkDependencies() {
        return checkDependency("Kingdoms", true, "1.10");
    }
    
    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new KingdomsXRegionHandler(this);
        }
        
        return this.regionHandler;
    }
}
