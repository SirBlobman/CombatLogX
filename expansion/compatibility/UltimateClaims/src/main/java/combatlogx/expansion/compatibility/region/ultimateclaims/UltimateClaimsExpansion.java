package combatlogx.expansion.compatibility.region.ultimateclaims;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class UltimateClaimsExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public UltimateClaimsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("UltimateClaims", true, "1");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerUltimateClaims(this);
        }

        return this.regionHandler;
    }
}
