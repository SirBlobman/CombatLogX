package combatlogx.expansion.compatibility.region.world.guard;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

import combatlogx.expansion.compatibility.region.world.guard.handler.WorldGuardRegionHandler;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import combatlogx.expansion.compatibility.region.world.guard.listener.ListenerWorldGuard;

public final class WorldGuardExpansion extends RegionExpansion {
    private final HookWorldGuard hookWorldGuard;
    private RegionHandler regionHandler;

    public WorldGuardExpansion(ICombatLogX plugin) {
        super(plugin);
        this.hookWorldGuard = new HookWorldGuard(this);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!checkDependency("WorldGuard", false)) {
            return;
        }

        HookWorldGuard hook = getHookWorldGuard();
        hook.registerFlags();
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("WorldGuard", true);
    }

    @Override
    public void afterEnable() {
        new ListenerWorldGuard(this).register();
    }

    @Override
    public RegionHandler getRegionHandler() {
        if(this.regionHandler == null) {
            this.regionHandler = new WorldGuardRegionHandler(this);
        }

        return this.regionHandler;
    }

    public HookWorldGuard getHookWorldGuard() {
        return this.hookWorldGuard;
    }
}
