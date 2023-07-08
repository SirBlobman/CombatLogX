package combatlogx.expansion.compatibility.region.kingdomsx;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class ExpansionKingdomsX extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public ExpansionKingdomsX(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Kingdoms", true, "1.");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerKingdomsX(this);
        }

        return this.regionHandler;
    }
}
