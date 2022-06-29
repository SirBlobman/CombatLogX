package combatlogx.expansion.compatibility.region.protectionstones;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class ProtectionStonesExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public ProtectionStonesExpansion(final ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        boolean dependencyPS = checkDependency("ProtectionStones", true);
        boolean dependencyWG = checkDependency("WorldGuard", true, "7");
        return (dependencyPS && dependencyWG);
    }

    @Override
    public RegionHandler getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new ProtectionStonesRegionHandler(this);
        }

        return this.regionHandler;
    }

    @Override
    public void afterEnable() {
        new ProtectionStonesListener(this).register();
    }
}
