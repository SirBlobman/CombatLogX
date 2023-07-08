package combatlogx.expansion.compatibility.region.husktowns;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class HuskTownsExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public HuskTownsExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("HuskTowns", true, "2.2");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerHuskTowns(this);
        }

        return this.regionHandler;
    }
}
