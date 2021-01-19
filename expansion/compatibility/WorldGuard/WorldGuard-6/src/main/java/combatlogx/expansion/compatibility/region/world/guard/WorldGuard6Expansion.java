package combatlogx.expansion.compatibility.region.world.guard;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

public final class WorldGuard6Expansion extends RegionExpansion {
    private RegionHandler regionHandler;
    public WorldGuard6Expansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        if(!checkDependency("WorldGuard", false, "6")) return;
        HookWorldGuard6.registerFlags(this);
    }

    @Override
    public void afterEnable() {
        this.regionHandler = new WorldGuard6RegionHandler(this);
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("WorldGuard", true, "6");
    }

    @Override
    public RegionHandler getRegionHandler() {
        return this.regionHandler;
    }
}