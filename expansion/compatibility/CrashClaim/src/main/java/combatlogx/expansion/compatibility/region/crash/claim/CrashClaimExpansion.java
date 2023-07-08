package combatlogx.expansion.compatibility.region.crash.claim;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class CrashClaimExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public CrashClaimExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("CrashClaim", true, "1.0");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new CrashClaimRegionHandler(this);
        }

        return this.regionHandler;
    }
}
