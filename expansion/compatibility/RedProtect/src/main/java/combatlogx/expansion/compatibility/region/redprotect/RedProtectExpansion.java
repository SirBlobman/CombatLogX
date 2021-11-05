package combatlogx.expansion.compatibility.region.redprotect;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class RedProtectExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    
    public RedProtectExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }
    
    @Override
    public boolean checkDependencies() {
        return checkDependency("RedProtect", true);
    }
    
    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new RedProtectRegionHandler(this);
        }
        
        return this.regionHandler;
    }
}
