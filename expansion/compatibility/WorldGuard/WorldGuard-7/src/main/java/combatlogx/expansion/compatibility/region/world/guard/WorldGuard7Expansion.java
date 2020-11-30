package combatlogx.expansion.compatibility.region.world.guard;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.region.RegionExpansion;
import com.SirBlobman.combatlogx.api.expansion.region.RegionHandler;

public final class WorldGuard7Expansion extends RegionExpansion {
    private RegionHandler regionHandler;
    public WorldGuard7Expansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        if(!checkDependency("WorldGuard", false, "6")) return;
        HookWorldGuard7.registerFlags(this);
    }

    @Override
    public void afterEnable() {
        this.regionHandler = new WorldGuard7RegionHandler(this);
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