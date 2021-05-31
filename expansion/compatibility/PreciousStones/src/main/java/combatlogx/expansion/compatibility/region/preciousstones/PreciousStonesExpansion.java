package combatlogx.expansion.compatibility.region.preciousstones;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class PreciousStonesExpansion extends RegionExpansion {
    private RegionHandler regionHandler;
    public PreciousStonesExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("PreciousStones", true, "15");
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new PreciousStonesRegionHandler(this);
        }

        return this.regionHandler;
    }

    @Override
    public void afterEnable() {
        new ListenerPreciousStones(this).register();
    }
}
