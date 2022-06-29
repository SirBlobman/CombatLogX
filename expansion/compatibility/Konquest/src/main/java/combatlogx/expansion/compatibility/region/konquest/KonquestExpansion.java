package combatlogx.expansion.compatibility.region.konquest;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class KonquestExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public KonquestExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Konquest", true, "0.5");
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new KonquestRegionHandler(this);
        }

        return this.regionHandler;
    }
}
