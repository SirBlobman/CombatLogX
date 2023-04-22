package combatlogx.expansion.compatibility.region.konquest;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class KonquestExpansion extends RegionExpansion {
    private RegionHandler<?> regionHandler;

    public KonquestExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("Konquest", true, "0.10");
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new RegionHandlerKonquest(this);
        }

        return this.regionHandler;
    }
}
