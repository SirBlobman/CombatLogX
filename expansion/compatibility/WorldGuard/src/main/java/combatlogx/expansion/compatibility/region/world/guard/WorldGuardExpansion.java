package combatlogx.expansion.compatibility.region.world.guard;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

import combatlogx.expansion.compatibility.region.world.guard.handler.WorldGuardRegionHandler;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import combatlogx.expansion.compatibility.region.world.guard.listener.ListenerWorldGuard;

public final class WorldGuardExpansion extends RegionExpansion {
    private RegionHandler regionHandler;

    public WorldGuardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!checkDependency("WorldGuard", false)) return;
        HookWorldGuard.registerFlags(this);
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("WorldGuard", true);
    }

    @Override
    public void afterEnable() {
        this.regionHandler = new WorldGuardRegionHandler(this);
        registerListener(new ListenerWorldGuard(this));
    }

    @Override
    public RegionHandler getRegionHandler() {
        return this.regionHandler;
    }
}
